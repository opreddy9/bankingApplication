package com.copart.bankingApplication.controller;

import com.copart.bankingApplication.dto.*;
import com.copart.bankingApplication.services.BankingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerDTO) {
        CustomerResponseDTO response = bankingService.createCustomer(customerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountDTO) {
        AccountResponseDTO response = bankingService.createAccount(accountDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{accountUuid}")
    public ResponseEntity<AccountResponseDTO> getAccountDetails(@PathVariable UUID accountUuid) {
        AccountResponseDTO response = bankingService.getAccountDetails(accountUuid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/{accountUuid}/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@PathVariable UUID accountUuid, 
                                                                        @Valid @RequestBody TransactionRequestDTO transactionDTO) {
        AccountResponseDTO response = bankingService.deposit(accountUuid, transactionDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/{accountUuid}/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@PathVariable UUID accountUuid, 
                                                                    @Valid @RequestBody TransactionRequestDTO transactionDTO) {
        AccountResponseDTO response = bankingService.withdraw(accountUuid, transactionDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accounts/{accountUuid}/inactivate")
    public ResponseEntity<String> inactivateAccount(@PathVariable UUID accountUuid) {
        bankingService.inactivateAccount(accountUuid);
        return ResponseEntity.ok("Account inactivated successfully");
    }

    @PutMapping("/accounts/{accountUuid}/activate")
    public ResponseEntity<String> activateAccount(@PathVariable UUID accountUuid) {
        bankingService.activateAccount(accountUuid);
        return ResponseEntity.ok("Account activated successfully");
    }

    @GetMapping("/accounts/getBalance/{accountUuid}")
    public ResponseEntity<BalanceResponseDTO> getBalanceInTheAccount(@Valid @PathVariable UUID accountUuid){
        BalanceResponseDTO response=bankingService.checkBalance(accountUuid);
        return ResponseEntity.ok(response);
    }
}