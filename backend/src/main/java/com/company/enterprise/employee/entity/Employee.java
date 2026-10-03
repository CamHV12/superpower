package com.company.enterprise.employee.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="employees")
public class Employee {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(name="full_name", nullable=false, length=150) private String fullName;
    @Column(nullable=false, unique=true, length=255) private String email;
    @Column(length=30) private String phone;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;

    protected Employee() {}
    public Employee(UUID id,String fullName,String email,String phone,boolean active) {
        this.id=id; this.fullName=fullName; this.email=email; this.phone=phone; this.active=active;
    }
    @PrePersist void onCreate(){ Instant now=Instant.now(); createdAt=now; updatedAt=now; }
    @PreUpdate void onUpdate(){ updatedAt=Instant.now(); }
    public UUID getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;}
    public String getPhone(){return phone;} public boolean isActive(){return active;}
    public void update(String fullName,String phone,boolean active){this.fullName=fullName;this.phone=phone;this.active=active;}
}