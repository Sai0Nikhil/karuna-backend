package com.karuna.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;      // lowercase: citizen | ngo | vet
    private String ngoName;
    private String clinicName;

    public LoginResponse() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String token; private Long userId; private String name;
        private String email; private String role; private String ngoName; private String clinicName;
        public Builder token(String v) { token = v; return this; }
        public Builder userId(Long v) { userId = v; return this; }
        public Builder name(String v) { name = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder role(String v) { role = v; return this; }
        public Builder ngoName(String v) { ngoName = v; return this; }
        public Builder clinicName(String v) { clinicName = v; return this; }
        public LoginResponse build() {
            LoginResponse r = new LoginResponse();
            r.token = token; r.userId = userId; r.name = name;
            r.email = email; r.role = role != null ? role.toLowerCase() : null; // always lowercase!
            r.ngoName = ngoName; r.clinicName = clinicName;
            return r;
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNgoName() { return ngoName; }
    public void setNgoName(String ngoName) { this.ngoName = ngoName; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
}
