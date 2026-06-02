package com.karuna.dto;

import jakarta.validation.constraints.*;

public class DonationRequest {
    @NotBlank private String donorName;
    @Min(1) private int amountInr;
    private String message;

    public DonationRequest() {}

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }
    public int getAmountInr() { return amountInr; }
    public void setAmountInr(int amountInr) { this.amountInr = amountInr; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
