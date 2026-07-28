package com.endustri.arizasistemi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ArizaKaydi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici bildirenKullanici;

    private String uretimHatti;
    private String arizaTuru;
    private String arizaTuruDiger;
    
    @Column(nullable = false, length = 1000)
    private String arizaAciklamasi; 
    
    private LocalDateTime arizaTarihiSaati;

    private boolean isgRiskiVarMi;
    private boolean durusVarMi;
    private boolean disaBagimliMi;

    private int oncelikPuani;
    private String oncelikSeviyesi;
    private String arizaDurumu = "Aktif";

    @PrePersist
    @PreUpdate
    public void hesaplaOncelik() {
        int puan = 0;
        if (this.isgRiskiVarMi) puan += 40;
        if (this.durusVarMi) {
            puan += 30;
            if ("Keçe".equals(this.uretimHatti)) puan += 20;
            else if ("Fitil".equals(this.uretimHatti) || "Kırma".equals(this.uretimHatti)) puan += 10;
            else if ("WR".equals(this.uretimHatti)) puan += 5;
        }
        if (this.disaBagimliMi) puan += 10;

        this.oncelikPuani = puan;
        if (puan >= 80) this.oncelikSeviyesi = "Kritik (Anında Müdahale)";
        else if (puan >= 50) this.oncelikSeviyesi = "Yüksek";
        else if (puan >= 30) this.oncelikSeviyesi = "Orta";
        else this.oncelikSeviyesi = "Düşük";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Kullanici getBildirenKullanici() { return bildirenKullanici; }
    public void setBildirenKullanici(Kullanici bildirenKullanici) { this.bildirenKullanici = bildirenKullanici; }
    public String getUretimHatti() { return uretimHatti; }
    public void setUretimHatti(String uretimHatti) { this.uretimHatti = uretimHatti; }
    public String getArizaTuru() { return arizaTuru; }
    public void setArizaTuru(String arizaTuru) { this.arizaTuru = arizaTuru; }
    public String getArizaTuruDiger() { return arizaTuruDiger; }
    public void setArizaTuruDiger(String arizaTuruDiger) { this.arizaTuruDiger = arizaTuruDiger; }
    public String getArizaAciklamasi() { return arizaAciklamasi; }
    public void setArizaAciklamasi(String arizaAciklamasi) { this.arizaAciklamasi = arizaAciklamasi; }
    public LocalDateTime getArizaTarihiSaati() { return arizaTarihiSaati; }
    public void setArizaTarihiSaati(LocalDateTime arizaTarihiSaati) { this.arizaTarihiSaati = arizaTarihiSaati; }
    public boolean isIsgRiskiVarMi() { return isgRiskiVarMi; }
    public void setIsgRiskiVarMi(boolean isgRiskiVarMi) { this.isgRiskiVarMi = isgRiskiVarMi; }
    public boolean isDurusVarMi() { return durusVarMi; }
    public void setDurusVarMi(boolean durusVarMi) { this.durusVarMi = durusVarMi; }
    public boolean isDisaBagimliMi() { return disaBagimliMi; }
    public void setDisaBagimliMi(boolean disaBagimliMi) { this.disaBagimliMi = disaBagimliMi; }
    public int getOncelikPuani() { return oncelikPuani; }
    public void setOncelikPuani(int oncelikPuani) { this.oncelikPuani = oncelikPuani; }
    public String getOncelikSeviyesi() { return oncelikSeviyesi; }
    public void setOncelikSeviyesi(String oncelikSeviyesi) { this.oncelikSeviyesi = oncelikSeviyesi; }
    public String getArizaDurumu() { return arizaDurumu; }
    public void setArizaDurumu(String arizaDurumu) { this.arizaDurumu = arizaDurumu; }
}