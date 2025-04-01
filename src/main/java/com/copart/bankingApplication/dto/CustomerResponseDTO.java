package com.copart.bankingApplication.dto;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String customerName;
    private String aadharNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String phoneNumber;
}