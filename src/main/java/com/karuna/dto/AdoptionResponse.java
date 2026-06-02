package com.karuna.dto;

import java.time.LocalDateTime;

public class AdoptionResponse {
    private Long id;
    private LocalDateTime ts;
    private String applicantName;
    private String contact;
    private String reason;
    private String status;

    public AdoptionResponse() {}

    public AdoptionResponse(Long id, LocalDateTime ts, String applicantName, String contact,
                            String reason, String status) {
        this.id = id; this.ts = ts; this.applicantName = applicantName;
        this.contact = contact; this.reason = reason; this.status = status;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime ts; private String applicantName;
        private String contact; private String reason; private String status;
        public Builder id(Long v) { id = v; return this; }
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder applicantName(String v) { applicantName = v; return this; }
        public Builder contact(String v) { contact = v; return this; }
        public Builder reason(String v) { reason = v; return this; }
        public Builder status(String v) { status = v; return this; }
        public AdoptionResponse build() { return new AdoptionResponse(id, ts, applicantName, contact, reason, status); }
    }

    public Long getId() { return id; }
    public LocalDateTime getTs() { return ts; }
    public String getApplicantName() { return applicantName; }
    public String getContact() { return contact; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}
