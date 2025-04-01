package com.copart.bankingApplication.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDTO {
    @NotNull(message = "Customer ID is mandatory")
    private Long customerId;

    @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;
}
