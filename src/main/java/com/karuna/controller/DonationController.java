package com.karuna.controller;

import com.karuna.dto.DonationRequest;
import com.karuna.dto.DonationResponse;
import com.karuna.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public ResponseEntity<List<DonationResponse>> getAll() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<DonationResponse>> getForCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(donationService.getDonationsForCase(caseId));
    }

    @PostMapping("/case/{caseId}")
    public ResponseEntity<?> donate(@PathVariable Long caseId,
                                     @Valid @RequestBody DonationRequest req) {
        try {
            return ResponseEntity.ok(donationService.createDonation(caseId, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
