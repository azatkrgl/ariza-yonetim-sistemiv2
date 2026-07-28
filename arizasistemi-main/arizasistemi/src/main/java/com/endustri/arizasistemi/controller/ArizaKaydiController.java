package com.endustri.arizasistemi.controller;

import com.endustri.arizasistemi.entity.ArizaKaydi;
import com.endustri.arizasistemi.entity.Kullanici;
import com.endustri.arizasistemi.repository.ArizaKaydiRepository;
import com.endustri.arizasistemi.repository.KullaniciRepository;
import com.endustri.arizasistemi.service.EmailService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession; 
import java.util.List;

@Controller
public class ArizaKaydiController {

    @Autowired
    private ArizaKaydiRepository arizaRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private EmailService emailService; 

    @GetMapping("/")
    public String anaGiris() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginEkrani() {
        return "login";
    }

    // 3 ROLE GÖRE YÖNLENDİRME
    @PostMapping("/login-kontrol")
    public String loginKontrol(@RequestParam String sicilNo, @RequestParam String sifre, HttpSession session, Model model) {
        Kullanici aktifKullanici = kullaniciRepository.findBySicilNoAndSifre(sicilNo, sifre);
        if (aktifKullanici != null) {
            session.setAttribute("aktifKullanici", aktifKullanici);
            
            if ("SUPER_ADMIN".equals(aktifKullanici.getRol())) {
                return "redirect:/super-admin"; 
            } else if ("ADMIN".equals(aktifKullanici.getRol())) {
                return "redirect:/admin";
            } else {
                return "redirect:/calisan";
            }
        } else {
            model.addAttribute("hata", "Sicil No veya Şifre Hatalı!"); 
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }

    // --- 1. ÇALIŞAN MODU ---
    @GetMapping("/calisan")
    public String calisanEkrani(Model model, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"CALISAN".equals(kullanici.getRol())) return "redirect:/login";
        model.addAttribute("arizaKaydi", new ArizaKaydi());
        return "calisan";
    }

    @PostMapping("/kaydet")
    public String kaydet(ArizaKaydi arizaKaydi, HttpSession session) {
        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null) return "redirect:/login";

        arizaKaydi.setBildirenKullanici(aktifKullanici);
        arizaKaydi.hesaplaOncelik(); 
        arizaRepository.save(arizaKaydi); 

        if (arizaKaydi.getOncelikPuani() >= 80) {
            String konu = "DİKKAT! ACİL ARIZA - " + arizaKaydi.getUretimHatti() + " Hattı";
            String icerik = "Kritik bir arıza meydana gelmiştir.\n\n"
                          + "Bildiren Personel: " + aktifKullanici.getAdSoyad() + " (Sicil: " + aktifKullanici.getSicilNo() + ")\n"
                          + "Sorunlu Hat: " + arizaKaydi.getUretimHatti() + "\n"
                          + "Detaylı Açıklama: " + arizaKaydi.getArizaAciklamasi() + "\n"
                          + "Öncelik Puanı: " + arizaKaydi.getOncelikPuani() + "/100\n";
            emailService.acilDurumMailiGonder("yonetici.mailin@gmail.com", konu, icerik);
        }
        return "redirect:/calisan?basarili"; 
    }

    // --- 2. YÖNETİCİ / TEKNİK MODU (ADMIN) ---
    @GetMapping("/admin")
    public String adminEkrani(Model model, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"ADMIN".equals(kullanici.getRol())) return "redirect:/login";
        List<ArizaKaydi> aktifKayitlar = arizaRepository.findByArizaDurumu("Aktif"); 
        model.addAttribute("kayitlar", aktifKayitlar);
        return "admin";
    }

    @GetMapping("/cozuldu/{id}")
    public String cozulduIsaretle(@PathVariable Long id, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"ADMIN".equals(kullanici.getRol())) return "redirect:/login";
        ArizaKaydi kayit = arizaRepository.findById(id).orElse(null);
        if(kayit != null) {
            kayit.setArizaDurumu("Çözüldü");
            arizaRepository.save(kayit); 
        }
        return "redirect:/admin";
    }

    @GetMapping("/analiz")
    public String analizEkrani(Model model, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"ADMIN".equals(kullanici.getRol())) return "redirect:/login";
        List<ArizaKaydi> cozulmusKayitlar = arizaRepository.findByArizaDurumu("Çözüldü"); 
        model.addAttribute("gecmisKayitlar", cozulmusKayitlar);
        return "analiz";
    }

    @GetMapping("/duzenle/{id}")
    public String duzenleEkrani(@PathVariable Long id, Model model, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"ADMIN".equals(kullanici.getRol())) return "redirect:/login";

        ArizaKaydi kayit = arizaRepository.findById(id).orElse(null);
        if (kayit == null) return "redirect:/admin";

        model.addAttribute("arizaKaydi", kayit);
        return "duzenle";
    }

    @PostMapping("/guncelle/{id}")
    public String arizaGuncelle(@PathVariable Long id, ArizaKaydi formVerisi, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"ADMIN".equals(kullanici.getRol())) return "redirect:/login";

        ArizaKaydi mevcutKayit = arizaRepository.findById(id).orElse(null);
        if (mevcutKayit != null) {
            mevcutKayit.setUretimHatti(formVerisi.getUretimHatti());
            mevcutKayit.setArizaTuru(formVerisi.getArizaTuru());
            mevcutKayit.setArizaTuruDiger(formVerisi.getArizaTuruDiger());
            mevcutKayit.setArizaAciklamasi(formVerisi.getArizaAciklamasi());
            mevcutKayit.setIsgRiskiVarMi(formVerisi.isIsgRiskiVarMi());
            mevcutKayit.setDurusVarMi(formVerisi.isDurusVarMi());
            mevcutKayit.setDisaBagimliMi(formVerisi.isDisaBagimliMi());

            mevcutKayit.hesaplaOncelik();
            arizaRepository.save(mevcutKayit);
        }
        return "redirect:/admin";
    }

    // --- 3. SÜPER ADMIN MODU ---
    @GetMapping("/super-admin")
    public String superAdminEkrani(Model model, HttpSession session) {
        Kullanici kullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (kullanici == null || !"SUPER_ADMIN".equals(kullanici.getRol())) return "redirect:/login";
        
        model.addAttribute("kullanicilar", kullaniciRepository.findAll());
        return "super-admin";
    }

    @PostMapping("/super-admin/kullanici/kaydet")
    public String kullaniciKaydet(Kullanici yeniKullanici, HttpSession session) {
        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null || !"SUPER_ADMIN".equals(aktifKullanici.getRol())) return "redirect:/login";
        
        kullaniciRepository.save(yeniKullanici);
        return "redirect:/super-admin";
    }

    @GetMapping("/super-admin/kullanici/sil/{id}")
    public String kullaniciSil(@PathVariable Long id, HttpSession session) {
        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null || !"SUPER_ADMIN".equals(aktifKullanici.getRol())) return "redirect:/login";
        
        // Kendini silmesini engelle
        if (aktifKullanici.getId().equals(id)) return "redirect:/super-admin";

        kullaniciRepository.deleteById(id);
        return "redirect:/super-admin";
    }
}