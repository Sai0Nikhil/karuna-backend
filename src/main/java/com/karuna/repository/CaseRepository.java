package com.karuna.repository;

import com.karuna.entity.Case;
import com.karuna.entity.Case.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findAllByOrderByCreatedAtDesc();
    List<Case> findByStatusNotInOrderByCreatedAtDesc(List<CaseStatus> excluded);
    List<Case> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    List<Case> findByNgoOrderByCreatedAtDesc(String ngo);
    long countByStatus(CaseStatus status);
}
