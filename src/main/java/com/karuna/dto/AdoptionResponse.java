package com.karuna.dto;

import java.time.LocalDateTime;

public class AdoptionResponse {
    private Long id;
    private LocalDateTime ts;
    private String applicantName;
    private String contact;
    private String reason;
    private String status;

    private String adopterIdUrl;
    private String checkinsLogs;

    public AdoptionResponse() {}

    public AdoptionResponse(Long id, LocalDateTime ts, String applicantName, String contact,
                            String reason, String status, String adopterIdUrl, String checkinsLogs) {
        this.id = id; this.ts = ts; this.applicantName = applicantName;
        this.contact = contact; this.reason = reason; this.status = status;
        this.adopterIdUrl = adopterIdUrl; this.checkinsLogs = checkinsLogs;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime ts; private String applicantName;
        private String contact; private String reason; private String status;
        private String adopterIdUrl; private String checkinsLogs;
        public Builder id(Long v) { id = v; return this; }
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder applicantName(String v) { applicantName = v; return this; }
        public Builder contact(String v) { contact = v; return this; }
        public Builder reason(String v) { reason = v; return this; }
        public Builder status(String v) { status = v; return this; }
        public Builder adopterIdUrl(String v) { adopterIdUrl = v; return this; }
        public Builder checkinsLogs(String v) { checkinsLogs = v; return this; }
        public AdoptionResponse build() { return new AdoptionResponse(id, ts, applicantName, contact, reason, status, adopterIdUrl, checkinsLogs); }
    }

    public Long getId() { return id; }
    public LocalDateTime getTs() { return ts; }
    public String getApplicantName() { return applicantName; }
    public String getContact() { return contact; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getAdopterIdUrl() { return adopterIdUrl; }
    public String getCheckinsLogs() { return checkinsLogs; }
}
