package com.karuna.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_application")
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime ts;
    private String applicantName;
    private String contact;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseRef;

    public enum AppStatus { pending, approved, rejected }

    public AdoptionApplication() {}

    public AdoptionApplication(Long id, LocalDateTime ts, String applicantName, String contact,
                               String reason, AppStatus status, Case caseRef) {
        this.id = id; this.ts = ts; this.applicantName = applicantName;
        this.contact = contact; this.reason = reason; this.status = status; this.caseRef = caseRef;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime ts; private String applicantName;
        private String contact; private String reason; private AppStatus status; private Case caseRef;
        public Builder id(Long v) { id = v; return this; }
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder applicantName(String v) { applicantName = v; return this; }
        public Builder contact(String v) { contact = v; return this; }
        public Builder reason(String v) { reason = v; return this; }
        public Builder status(AppStatus v) { status = v; return this; }
        public Builder caseRef(Case v) { caseRef = v; return this; }
        public AdoptionApplication build() { return new AdoptionApplication(id, ts, applicantName, contact, reason, status, caseRef); }
    }

    @PrePersist
    public void prePersist() {
        if (ts == null) ts = LocalDateTime.now();
        if (status == null) status = AppStatus.pending;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public AppStatus getStatus() { return status; }
    public void setStatus(AppStatus status) { this.status = status; }
    public Case getCaseRef() { return caseRef; }
    public void setCaseRef(Case caseRef) { this.caseRef = caseRef; }
}
