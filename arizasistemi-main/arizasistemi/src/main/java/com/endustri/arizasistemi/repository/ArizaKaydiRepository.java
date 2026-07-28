package com.endustri.arizasistemi.repository;

import com.endustri.arizasistemi.entity.ArizaKaydi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArizaKaydiRepository extends JpaRepository<ArizaKaydi, Long> {
    List<ArizaKaydi> findByArizaDurumu(String arizaDurumu);
}