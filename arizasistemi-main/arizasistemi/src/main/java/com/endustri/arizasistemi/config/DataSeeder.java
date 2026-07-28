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
        // Eğer veritabanında hiç kullanıcı yoksa 55 kişiyi oluştur
        if (kullaniciRepository.count() == 0) {
            List<Kullanici> kullanicilar = new ArrayList<>();

            // 5 Yönetici
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

        // Eğer hiç arıza kaydı yoksa rastgele 100 örnek arıza üret
        if (arizaRepository.count() == 0) {
            List<Kullanici> calisanlar = kullaniciRepository.findAll();
            // Sadece çalışanları filtrele (Adminler arıza girmemiş olsun)
            calisanlar.removeIf(k -> "ADMIN".equals(k.getRol()));

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
                    ArizaKaydi arıza = new ArizaKaydi();
                    
                    // Rastgele bir çalışan ata
                    arıza.setBildirenKullanici(calisanlar.get(random.nextInt(calisanlar.size())));
                    
                    // Rastgele hat ve tür seç
                    arıza.setUretimHatti(hatlar[random.nextInt(hatlar.length)]);
                    String secilenTur = turler[random.nextInt(turler.length)];
                    arıza.setArizaTuru(secilenTur);
                    
                    if ("Diğer".equals(secilenTur)) {
                        arıza.setArizaTuruDiger("Hidrolik Basınç Düşüşü");
                    }

                    arıza.setArizaAciklamasi(aciklamalar[random.nextInt(aciklamalar.length)]);
                    
                    // Geçmişe dönük rastgele tarihler (Son 30 gün içinde)
                    arıza.setArizaTarihiSaati(LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24)));

                    // Rastgele kritik parametreler (%30 ihtimalle true olur)
                    arıza.setIsgRiskiVarMi(random.nextDouble() < 0.25);
                    arıza.setDurusVarMi(random.nextDouble() < 0.40);
                    arıza.setDisaBagimliMi(random.nextDouble() < 0.20);

                    // Puanı tetikle
                    arıza.hesaplaOncelik();

                    // Rastgele olarak bir kısmı çözülmüş, bir kısmı aktif olsun
                    if (random.nextBoolean()) {
                        arıza.setArizaDurumu("Çözüldü");
                    } else {
                        arıza.setArizaDurumu("Aktif");
                    }

                    rastgeleArizalar.add(arıza);
                }

                arizaRepository.saveAll(rastgeleArizalar);
                System.out.println("--- 100 ADET RASTGELE ÖRNEK ARIZA KAYDI OLUŞTURULDU ---");
            }
        }
    }
}