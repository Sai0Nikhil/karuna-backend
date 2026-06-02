package com.karuna.repository;

import com.karuna.entity.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    List<AdoptionApplication> findByCaseRefIdOrderByTsDesc(Long caseId);
    List<AdoptionApplication> findAllByOrderByTsDesc();
}
