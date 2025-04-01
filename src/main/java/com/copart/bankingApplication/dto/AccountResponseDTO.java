package com.copart.bankingApplication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountResponseDTO {
    private Long id;
    private UUID accountUuid;
    private Long customerId;
    private BigDecimal balance;
    private String status;
}
