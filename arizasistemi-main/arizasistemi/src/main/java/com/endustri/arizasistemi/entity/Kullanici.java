package com.endustri.arizasistemi.entity;

import jakarta.persistence.*;

@Entity
public class Kullanici {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sicilNo; 

    private String adSoyad;
    private String sifre;
    private String rol; 

    public Kullanici() {}

    public Kullanici(String sicilNo, String adSoyad, String sifre, String rol) {
        this.sicilNo = sicilNo;
        this.adSoyad = adSoyad;
        this.sifre = sifre;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSicilNo() { return sicilNo; }
    public void setSicilNo(String sicilNo) { this.sicilNo = sicilNo; }
    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }
    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}