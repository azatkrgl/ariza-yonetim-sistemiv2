package com.endustri.arizasistemi.repository;

import com.endustri.arizasistemi.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    Kullanici findBySicilNoAndSifre(String sicilNo, String sifre);
}