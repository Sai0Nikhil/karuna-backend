package com.karuna.dto;

import java.time.LocalDateTime;

public class CaseEventResponse {
    private LocalDateTime ts;
    private String type;
    private String actor;
    private String details;

    public CaseEventResponse() {}

    public CaseEventResponse(LocalDateTime ts, String type, String actor, String details) {
        this.ts = ts; this.type = type; this.actor = actor; this.details = details;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private LocalDateTime ts; private String type; private String actor; private String details;
        public Builder ts(LocalDateTime v) { ts = v; return this; }
        public Builder type(String v) { type = v; return this; }
        public Builder actor(String v) { actor = v; return this; }
        public Builder details(String v) { details = v; return this; }
        public CaseEventResponse build() { return new CaseEventResponse(ts, type, actor, details); }
    }

    public LocalDateTime getTs() { return ts; }
    public String getType() { return type; }
    public String getActor() { return actor; }
    public String getDetails() { return details; }
}
