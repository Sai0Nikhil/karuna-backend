package com.karuna.dto;

public class CaseRequest {
    private String imageDataUrl;
    private Double latitude;
    private Double longitude;
    private String locationLabel;
    private String species;
    private String injuryType;
    private String severity;
    private String probableCondition;
    private String firstAidSteps;
    private Integer estimatedCostInr;

    public CaseRequest() {}

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
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getProbableCondition() { return probableCondition; }
    public void setProbableCondition(String probableCondition) { this.probableCondition = probableCondition; }
    public String getFirstAidSteps() { return firstAidSteps; }
    public void setFirstAidSteps(String firstAidSteps) { this.firstAidSteps = firstAidSteps; }
    public Integer getEstimatedCostInr() { return estimatedCostInr; }
    public void setEstimatedCostInr(Integer estimatedCostInr) { this.estimatedCostInr = estimatedCostInr; }
}
