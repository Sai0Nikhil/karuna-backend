package com.karuna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String ngoName;
    private String clinicName; // for VET role
    private Boolean available;

    public enum Role { CITIZEN, NGO, VET, VOLUNTEER, ADMIN }

    public User() {}

    public User(Long id, String email, String password, String name, String phone, Role role, String ngoName, Boolean available) {
        this.id = id; this.email = email; this.password = password; this.name = name;
        this.phone = phone; this.role = role; this.ngoName = ngoName; this.available = available;
    }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String email; private String password; private String name;
        private String phone; private Role role; private String ngoName; private Boolean available;
        public Builder id(Long v) { id = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder password(String v) { password = v; return this; }
        public Builder name(String v) { name = v; return this; }
        public Builder phone(String v) { phone = v; return this; }
        public Builder role(Role v) { role = v; return this; }
        public Builder ngoName(String v) { ngoName = v; return this; }
        private String clinicName;
        public Builder clinicName(String v) { clinicName = v; return this; }
        public Builder available(Boolean v) { available = v; return this; }
        public User build() {
            User u = new User(id, email, password, name, phone, role, ngoName, available);
            u.setClinicName(clinicName);
            return u;
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getNgoName() { return ngoName; }
    public void setNgoName(String ngoName) { this.ngoName = ngoName; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
}
