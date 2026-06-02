package com.karuna.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime ts;
    private String donorName;
    private Integer amountInr;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseRef;

    public Donation() {}

    public Donation(Long id, LocalDateTime ts, String donorName, Integer amountInr, String message, Case caseRef) {
        this.id = id; this.ts = ts; this.donorName = donorName;
        this.amountInr = amountInr; this.message = message; this.caseRef = caseRef;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime ts; private String donorName;
        private Integer amountInr; private String message; private Case caseRef;
        public Builder id(Long v) { id = v; return this; }
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder donorName(String v) { donorName = v; return this; }
        public Builder amountInr(Integer v) { amountInr = v; return this; }
        public Builder message(String v) { message = v; return this; }
        public Builder caseRef(Case v) { caseRef = v; return this; }
        public Donation build() { return new Donation(id, ts, donorName, amountInr, message, caseRef); }
    }

    @PrePersist
    public void prePersist() { if (ts == null) ts = LocalDateTime.now(); }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }
    public Integer getAmountInr() { return amountInr; }
    public void setAmountInr(Integer amountInr) { this.amountInr = amountInr; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Case getCaseRef() { return caseRef; }
    public void setCaseRef(Case caseRef) { this.caseRef = caseRef; }
}
