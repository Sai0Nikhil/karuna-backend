package com.karuna.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "animal_case")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String reporterName;

    private String reporterContact;

    @Column(columnDefinition = "TEXT")
    private String imageDataUrl;

    private Double latitude;
    private Double longitude;
    private String locationLabel;
    private String species;
    private String injuryType;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(columnDefinition = "TEXT")
    private String probableCondition;

    @Column(columnDefinition = "TEXT")
    private String firstAidSteps;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private String assignedResponder;
    private String ngo;
    private Integer estimatedCostInr;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    public enum Severity { critical, urgent, routine }
    public enum CaseStatus { reported, assigned, collected, at_clinic, in_treatment, discharged, adopted, released }

    public Case() {}

    public Case(Long id, LocalDateTime createdAt, String reporterName, String reporterContact,
                String imageDataUrl, Double latitude, Double longitude, String locationLabel,
                String species, String injuryType, Severity severity, String probableCondition,
                String firstAidSteps, CaseStatus status, String assignedResponder, String ngo,
                Integer estimatedCostInr, String notes, User reporter) {
        this.id = id; this.createdAt = createdAt; this.reporterName = reporterName;
        this.reporterContact = reporterContact; this.imageDataUrl = imageDataUrl;
        this.latitude = latitude; this.longitude = longitude; this.locationLabel = locationLabel;
        this.species = species; this.injuryType = injuryType; this.severity = severity;
        this.probableCondition = probableCondition; this.firstAidSteps = firstAidSteps;
        this.status = status; this.assignedResponder = assignedResponder; this.ngo = ngo;
        this.estimatedCostInr = estimatedCostInr; this.notes = notes; this.reporter = reporter;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private LocalDateTime createdAt; private String reporterName;
        private String reporterContact; private String imageDataUrl; private Double latitude;
        private Double longitude; private String locationLabel; private String species;
        private String injuryType; private Severity severity; private String probableCondition;
        private String firstAidSteps; private CaseStatus status; private String assignedResponder;
        private String ngo; private Integer estimatedCostInr; private String notes; private User reporter;
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
        public Builder severity(Severity v) { severity = v; return this; }
        public Builder probableCondition(String v) { probableCondition = v; return this; }
        public Builder firstAidSteps(String v) { firstAidSteps = v; return this; }
        public Builder status(CaseStatus v) { status = v; return this; }
        public Builder assignedResponder(String v) { assignedResponder = v; return this; }
        public Builder ngo(String v) { ngo = v; return this; }
        public Builder estimatedCostInr(Integer v) { estimatedCostInr = v; return this; }
        public Builder notes(String v) { notes = v; return this; }
        public Builder reporter(User v) { reporter = v; return this; }
        public Case build() { return new Case(id, createdAt, reporterName, reporterContact, imageDataUrl,
                latitude, longitude, locationLabel, species, injuryType, severity, probableCondition,
                firstAidSteps, status, assignedResponder, ngo, estimatedCostInr, notes, reporter); }
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = CaseStatus.reported;
        if (notes == null) notes = "[]";
        if (firstAidSteps == null) firstAidSteps = "[]";
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getReporterContact() { return reporterContact; }
    public void setReporterContact(String reporterContact) { this.reporterContact = reporterContact; }
    public String getImageDataUrl() { return imageDataUrl; }
    public void setImageDataUrl(String imageDataUrl) { this.imageDataUrl = imageDataUrl; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getLocationLabel() { return locationLabel; }
    public void setLocationLabel(String locationLabel) { this.locationLabel = locationLabel; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String injuryType) { this.injuryType = injuryType; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getProbableCondition() { return probableCondition; }
    public void setProbableCondition(String probableCondition) { this.probableCondition = probableCondition; }
    public String getFirstAidSteps() { return firstAidSteps; }
    public void setFirstAidSteps(String firstAidSteps) { this.firstAidSteps = firstAidSteps; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public String getAssignedResponder() { return assignedResponder; }
    public void setAssignedResponder(String assignedResponder) { this.assignedResponder = assignedResponder; }
    public String getNgo() { return ngo; }
    public void setNgo(String ngo) { this.ngo = ngo; }
    public Integer getEstimatedCostInr() { return estimatedCostInr; }
    public void setEstimatedCostInr(Integer estimatedCostInr) { this.estimatedCostInr = estimatedCostInr; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }
}
