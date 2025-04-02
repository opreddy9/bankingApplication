package com.copart.bankingApplication.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BalanceResponseDTO {
    BigDecimal amount;
}
