package com.karuna.service;

import com.karuna.dto.DonationRequest;
import com.karuna.dto.DonationResponse;
import com.karuna.entity.Donation;
import com.karuna.repository.CaseRepository;
import com.karuna.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository donationRepo;
    private final CaseRepository caseRepo;

    public DonationService(DonationRepository donationRepo, CaseRepository caseRepo) {
        this.donationRepo = donationRepo;
        this.caseRepo = caseRepo;
    }

    public List<DonationResponse> getAllDonations() {
        return donationRepo.findAllByOrderByTsDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<DonationResponse> getDonationsForCase(Long caseId) {
        return donationRepo.findByCaseRefIdOrderByTsDesc(caseId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public DonationResponse createDonation(Long caseId, DonationRequest req) {
        var c = caseRepo.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        Donation d = Donation.builder()
                .ts(LocalDateTime.now())
                .donorName(req.getDonorName())
                .amountInr(req.getAmountInr())
                .message(req.getMessage())
                .paymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : "UPI")
                .billOffsetDetails(req.getBillOffsetDetails())
                .caseRef(c)
                .build();
        d = donationRepo.save(d);
        return toResponse(d);
    }

    private DonationResponse toResponse(Donation d) {
        return DonationResponse.builder()
                .id(d.getId()).ts(d.getTs()).donorName(d.getDonorName())
                .amountInr(d.getAmountInr()).message(d.getMessage())
                .paymentMethod(d.getPaymentMethod())
                .billOffsetDetails(d.getBillOffsetDetails())
                .build();
    }
}
