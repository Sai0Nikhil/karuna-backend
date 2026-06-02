package com.karuna.dto;

import java.time.LocalDateTime;

public class DonationResponse {
    private Long id;
    private LocalDateTime ts;
    private String donorName;
    private Integer amountInr;
    private String message;

    public DonationResponse() {}

    public DonationResponse(Long id, LocalDateTime ts, String donorName, Integer amountInr, String message) {
        this.id = id; this.ts = ts; this.donorName = donorName;
        this.amountInr = amountInr; this.message = message;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime ts; private String donorName;
        private Integer amountInr; private String message;
        public Builder id(Long v) { id = v; return this; }
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder donorName(String v) { donorName = v; return this; }
        public Builder amountInr(Integer v) { amountInr = v; return this; }
        public Builder message(String v) { message = v; return this; }
        public DonationResponse build() { return new DonationResponse(id, ts, donorName, amountInr, message); }
    }

    public Long getId() { return id; }
    public LocalDateTime getTs() { return ts; }
    public String getDonorName() { return donorName; }
    public Integer getAmountInr() { return amountInr; }
    public String getMessage() { return message; }
}
