package com.karuna.repository;

import com.karuna.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByCaseRefIdOrderByTsDesc(Long caseId);
    List<Donation> findAllByOrderByTsDesc();
}
