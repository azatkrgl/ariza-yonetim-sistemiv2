package com.endustri.arizasistemi.config;

import com.endustri.arizasistemi.entity.ArizaKaydi;
import com.endustri.arizasistemi.entity.Kullanici;
import com.endustri.arizasistemi.repository.ArizaKaydiRepository;
import com.endustri.arizasistemi.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private ArizaKaydiRepository arizaRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. ADIM: SÜPER ADMİNİ HER HALÜKARDA KONTROL ET VE YOKSA ZORLA EKLE
        boolean superAdminVar = false;
        for (Kullanici k : kullaniciRepository.findAll()) {
            if ("9999".equals(k.getSicilNo())) {
                superAdminVar = true;
                break;
            }
        }
        
        if (!superAdminVar) {
            kullaniciRepository.save(new Kullanici("9999", "Süper Yöneticim", "super123", "SUPER_ADMIN"));
            System.out.println("--- EKSİK OLAN SÜPER ADMIN (9999) SİSTEME ZORLA EKLENDİ ---");
        }

        // 2. ADIM: DİĞER KULLANICILARIN EKLENMESİ (Sadece içerisi boşsa veya sadece 1 kişi varsa)
        if (kullaniciRepository.count() <= 1) {
            List<Kullanici> kullanicilar = new ArrayList<>();

            // 5 Yönetici (Sadece Arıza Paneli)
            kullanicilar.add(new Kullanici("1001", "Ahmet Yılmaz", "admin123", "ADMIN"));
            kullanicilar.add(new Kullanici("1002", "Ayşe Kaya", "admin123", "ADMIN"));
            kullanicilar.add(new Kullanici("1003", "Mehmet Demir", "admin123", "ADMIN"));
            kullanicilar.add(new Kullanici("1004", "Fatma Çelik", "admin123", "ADMIN"));
            kullanicilar.add(new Kullanici("1005", "Mustafa Şahin", "admin123", "ADMIN"));

            // 50 Saha Çalışanı
            String[] isimler = {"Ali", "Ayşe", "Hasan", "Hüseyin", "Zeynep", "Elif", "Burak", "Kadir", "Kemal", "Deniz", "Derya", "Gökhan", "Hakan", "İbrahim", "Kaan", "Levent", "Murat", "Okan", "Ömer", "Yusuf", "Ceren", "Merve", "Gizem", "Emre", "Can"};
            String[] soyisimler = {"Öztürk", "Arslan", "Doğan", "Kılıç", "Çetin", "Kara", "Koç", "Kurt", "Özdemir", "Yıldırım", "Güneş", "Bozkurt", "Yıldız", "Aydın", "Polat", "Şahin", "Çelik", "Demir", "Kaya", "Yılmaz"};

            Random random = new Random();
            for (int i = 1; i <= 50; i++) {
                String sicilNo = String.valueOf(2000 + i);
                String adSoyad = isimler[random.nextInt(isimler.length)] + " " + soyisimler[random.nextInt(soyisimler.length)];
                kullanicilar.add(new Kullanici(sicilNo, adSoyad, "1234", "CALISAN"));
            }

            kullaniciRepository.saveAll(kullanicilar);
            System.out.println("--- 5 ADMIN VE 50 PERSONEL VERITABANINA AKTARILDI ---");
        }

        // 3. ADIM: ARIZA VERİLERİNİ EKLE
        if (arizaRepository.count() == 0) {
            List<Kullanici> calisanlar = kullaniciRepository.findAll();
            calisanlar.removeIf(k -> "ADMIN".equals(k.getRol()) || "SUPER_ADMIN".equals(k.getRol()));

            if (!calisanlar.isEmpty()) {
                List<ArizaKaydi> rastgeleArizalar = new ArrayList<>();
                Random random = new Random();

                String[] hatlar = {"Keçe", "Fitil", "Kırma", "WR"};
                String[] turler = {"Mekanik", "Elektrik", "Otomasyon", "Diğer"};
                String[] aciklamalar = {
                    "Rulmanlar aşırı ısınma yapıyor, acil kontrol edilmeli.",
                    "Sensör sinyali kesti, hat otomatik duruşa geçti.",
                    "Konveyör bant kayışı yerinden çıktı.",
                    "Pnömatik Valf hava kaçağı veriyor.",
                    "Ana motordan anormal sesler gelmektedir.",
                    "PLC ekranında haberleşme kopması hatası alındı.",
                    "Kesici bıçak ucunda körelme ve çapaklanma var."
                };

                for (int i = 1; i <= 100; i++) {
                    ArizaKaydi ariza = new ArizaKaydi();
                    ariza.setBildirenKullanici(calisanlar.get(random.nextInt(calisanlar.size())));
                    ariza.setUretimHatti(hatlar[random.nextInt(hatlar.length)]);
                    String secilenTur = turler[random.nextInt(turler.length)];
                    ariza.setArizaTuru(secilenTur);
                    
                    if ("Diğer".equals(secilenTur)) {
                        ariza.setArizaTuruDiger("Hidrolik Basınç Düşüşü");
                    }

                    ariza.setArizaAciklamasi(aciklamalar[random.nextInt(aciklamalar.length)]);
                    ariza.setArizaTarihiSaati(LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24)));
                    ariza.setIsgRiskiVarMi(random.nextDouble() < 0.25);
                    ariza.setDurusVarMi(random.nextDouble() < 0.40);
                    ariza.setDisaBagimliMi(random.nextDouble() < 0.20);
                    ariza.hesaplaOncelik();

                    if (random.nextBoolean()) {
                        ariza.setArizaDurumu("Çözüldü");
                    } else {
                        ariza.setArizaDurumu("Aktif");
                    }

                    rastgeleArizalar.add(ariza);
                }
                arizaRepository.saveAll(rastgeleArizalar);
                System.out.println("--- 100 ADET RASTGELE ÖRNEK ARIZA KAYDI OLUŞTURULDU ---");
            }
        }
    }
}