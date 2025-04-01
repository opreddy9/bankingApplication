package com.copart.bankingApplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String customerName;

    @NotBlank(message = "Aadhaar number is mandatory")
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Invalid Aadhaar number")
    @Column(unique = true, nullable = false)
    private String aadharNumber;

    @NotBlank(message = "Address Line 1 is mandatory")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "Country is mandatory")
    private String country;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^91[6-9][0-9]{9}$", message = "Invalid Indian phone number (must start with 91 followed by 10 digits starting with 6-9)")
    private String phoneNumber;
}
