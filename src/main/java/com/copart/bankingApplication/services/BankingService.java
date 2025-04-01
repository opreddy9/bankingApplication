package com.copart.bankingApplication.services;

import com.copart.bankingApplication.dto.*;

import java.util.UUID;

public interface BankingService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO);
    AccountResponseDTO createAccount(AccountRequestDTO accountDTO);
    AccountResponseDTO getAccountDetails(UUID accountUuid);
    AccountResponseDTO deposit(UUID accountUuid, TransactionRequestDTO transactionDTO);
    AccountResponseDTO withdraw(UUID accountUuid, TransactionRequestDTO transactionDTO);
    void inactivateAccount(UUID accountUuid);
    void activateAccount(UUID accountUuid);
}
