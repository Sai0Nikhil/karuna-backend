package com.karuna.dto;

import jakarta.validation.constraints.*;

public class DonationRequest {
    @NotBlank private String donorName;
    @Min(1) private int amountInr;
    private String message;

    private String paymentMethod;
    private String billOffsetDetails;

    public DonationRequest() {}

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }
    public int getAmountInr() { return amountInr; }
    public void setAmountInr(int amountInr) { this.amountInr = amountInr; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getBillOffsetDetails() { return billOffsetDetails; }
    public void setBillOffsetDetails(String billOffsetDetails) { this.billOffsetDetails = billOffsetDetails; }
}
