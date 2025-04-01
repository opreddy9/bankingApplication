package com.copart.bankingApplication.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    @NotBlank(message = "Customer name is mandatory")
    @Size(min = 2, max = 50)
    private String customerName;

    @NotBlank(message = "Aadhaar number is mandatory")
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$")
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
    @Pattern(regexp = "^91[6-9][0-9]{9}$")
    private String phoneNumber;
}
