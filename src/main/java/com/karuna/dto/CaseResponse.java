package com.karuna.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CaseResponse {
    private Long id;
    private LocalDateTime createdAt;
    private String reporterName;
    private String reporterContact;
    private String imageDataUrl;
    private Double latitude;
    private Double longitude;
    private String locationLabel;
    private String species;
    private String injuryType;
    private String severity;
    private String probableCondition;
    private List<String> firstAidSteps;
    private String status;
    private String assignedResponder;
    private String ngo;
    private Integer estimatedCostInr;
    private List<DonationResponse> donations;
    private List<AdoptionResponse> adoptionApplications;
    private List<String> notes;
    private List<CaseEventResponse> events;

    public CaseResponse() {}

    public CaseResponse(Long id, LocalDateTime createdAt, String reporterName, String reporterContact,
                        String imageDataUrl, Double latitude, Double longitude, String locationLabel,
                        String species, String injuryType, String severity, String probableCondition,
                        List<String> firstAidSteps, String status, String assignedResponder, String ngo,
                        Integer estimatedCostInr, List<DonationResponse> donations,
                        List<AdoptionResponse> adoptionApplications, List<String> notes, List<CaseEventResponse> events) {
        this.id = id; this.createdAt = createdAt; this.reporterName = reporterName;
        this.reporterContact = reporterContact; this.imageDataUrl = imageDataUrl;
        this.latitude = latitude; this.longitude = longitude; this.locationLabel = locationLabel;
        this.species = species; this.injuryType = injuryType; this.severity = severity;
        this.probableCondition = probableCondition; this.firstAidSteps = firstAidSteps;
        this.status = status; this.assignedResponder = assignedResponder; this.ngo = ngo;
        this.estimatedCostInr = estimatedCostInr; this.donations = donations;
        this.adoptionApplications = adoptionApplications; this.notes = notes; this.events = events;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime createdAt; private String reporterName;
        private String reporterContact; private String imageDataUrl; private Double latitude;
        private Double longitude; private String locationLabel; private String species;
        private String injuryType; private String severity; private String probableCondition;
        private List<String> firstAidSteps; private String status; private String assignedResponder;
        private String ngo; private Integer estimatedCostInr; private List<DonationResponse> donations;
        private List<AdoptionResponse> adoptionApplications; private List<String> notes;
        private List<CaseEventResponse> events;
        public Builder id(Long v) { id = v; return this; }
        public Builder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public Builder reporterName(String v) { reporterName = v; return this; }
        public Builder reporterContact(String v) { reporterContact = v; return this; }
        public Builder imageDataUrl(String v) { imageDataUrl = v; return this; }
        public Builder latitude(Double v) { latitude = v; return this; }
        public Builder longitude(Double v) { longitude = v; return this; }
        public Builder locationLabel(String v) { locationLabel = v; return this; }
        public Builder species(String v) { species = v; return this; }
        public Builder injuryType(String v) { injuryType = v; return this; }
        public Builder severity(String v) { severity = v; return this; }
        public Builder probableCondition(String v) { probableCondition = v; return this; }
        public Builder firstAidSteps(List<String> v) { firstAidSteps = v; return this; }
        public Builder status(String v) { status = v; return this; }
        public Builder assignedResponder(String v) { assignedResponder = v; return this; }
        public Builder ngo(String v) { ngo = v; return this; }
        public Builder estimatedCostInr(Integer v) { estimatedCostInr = v; return this; }
        public Builder donations(List<DonationResponse> v) { donations = v; return this; }
        public Builder adoptionApplications(List<AdoptionResponse> v) { adoptionApplications = v; return this; }
        public Builder notes(List<String> v) { notes = v; return this; }
        public Builder events(List<CaseEventResponse> v) { events = v; return this; }
        public CaseResponse build() { return new CaseResponse(id, createdAt, reporterName, reporterContact,
                imageDataUrl, latitude, longitude, locationLabel, species, injuryType, severity,
                probableCondition, firstAidSteps, status, assignedResponder, ngo, estimatedCostInr,
                donations, adoptionApplications, notes, events); }
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getReporterName() { return reporterName; }
    public String getReporterContact() { return reporterContact; }
    public String getImageDataUrl() { return imageDataUrl; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocationLabel() { return locationLabel; }
    public String getSpecies() { return species; }
    public String getInjuryType() { return injuryType; }
    public String getSeverity() { return severity; }
    public String getProbableCondition() { return probableCondition; }
    public List<String> getFirstAidSteps() { return firstAidSteps; }
    public String getStatus() { return status; }
    public String getAssignedResponder() { return assignedResponder; }
    public String getNgo() { return ngo; }
    public Integer getEstimatedCostInr() { return estimatedCostInr; }
    public List<DonationResponse> getDonations() { return donations; }
    public List<AdoptionResponse> getAdoptionApplications() { return adoptionApplications; }
    public List<String> getNotes() { return notes; }
    public List<CaseEventResponse> getEvents() { return events; }
}
