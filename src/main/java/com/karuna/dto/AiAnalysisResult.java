package com.karuna.dto;

/**
 * Structured result from Gemini AI analysis of a reported animal case.
 */
public class AiAnalysisResult {

    private String probableCondition;   // e.g. "Fractured right hind leg, possible internal bleeding"
    private String severity;            // "critical" | "urgent" | "routine"
    private String injuryType;          // e.g. "Fracture / Road accident"
    private String firstAidSteps;       // JSON array string of step-by-step instructions
    private Integer estimatedCostInr;   // e.g. 4500
    private String aiSummary;           // 1–2 sentence plain-English summary for NGO
    private String confidence;          // "high" | "medium" | "low"
    private String medicines;           // JSON array of {name, dosage, route, frequency, notes}
    private String localSupport;        // JSON array of {name, address, phone}
    private String disclaimer;          // safety warning text

    public AiAnalysisResult() {}

    // ─── Getters & Setters ─────────────────────────────────────────────
    public String getProbableCondition() { return probableCondition; }
    public void setProbableCondition(String v) { this.probableCondition = v; }

    public String getSeverity() { return severity; }
    public void setSeverity(String v) { this.severity = v; }

    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String v) { this.injuryType = v; }

    public String getFirstAidSteps() { return firstAidSteps; }
    public void setFirstAidSteps(String v) { this.firstAidSteps = v; }

    public Integer getEstimatedCostInr() { return estimatedCostInr; }
    public void setEstimatedCostInr(Integer v) { this.estimatedCostInr = v; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String v) { this.aiSummary = v; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String v) { this.confidence = v; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String v) { this.medicines = v; }

    public String getLocalSupport() { return localSupport; }
    public void setLocalSupport(String v) { this.localSupport = v; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String v) { this.disclaimer = v; }
}
