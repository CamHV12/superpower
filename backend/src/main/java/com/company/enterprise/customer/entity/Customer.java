package com.company.enterprise.customer.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerType type;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "tax_code", unique = true, length = 30)
    private String taxCode;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Customer() {}

    public Customer(String code, String name, CustomerType type, String email,
                    String phone, String taxCode, String contactPerson, String address) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.taxCode = taxCode;
        this.contactPerson = contactPerson;
        this.address = address;
        this.active = true;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public CustomerType getType() { return type; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getTaxCode() { return taxCode; }
    public String getContactPerson() { return contactPerson; }
    public String getAddress() { return address; }
    public boolean isActive() { return active; }

    public void update(String name, CustomerType type, String email, String phone,
                       String taxCode, String contactPerson, String address, boolean active) {
        this.name = name;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.taxCode = taxCode;
        this.contactPerson = contactPerson;
        this.address = address;
        this.active = active;
    }
}
