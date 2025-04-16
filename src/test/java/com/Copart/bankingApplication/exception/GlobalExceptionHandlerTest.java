package com.Copart.bankingApplication.exception;

import com.copart.bankingApplication.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAccountNotFound_returnsNotFound() {
        AccountNotFoundException ex = new AccountNotFoundException("Account not found");

        ResponseEntity<String> response = handler.handleAccountNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", response.getBody());
    }

    @Test
    void handleCustomerNotFound_returnsNotFound() {
        CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found");

        ResponseEntity<String> response = handler.handleCustomerNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
    }

    @Test
    void handleInsufficientBalance_returnsBadRequest() {
        InsufficientBalanceException ex = new InsufficientBalanceException("Insufficient balance");

        ResponseEntity<String> response = handler.handleInsufficientBalance(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient balance", response.getBody());
    }

    @Test
    void handleInvalidAmount_returnsBadRequest() {
        InvalidAmountException ex = new InvalidAmountException("Invalid amount");

        ResponseEntity<String> response = handler.handleInvalidAmount(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    void handleInactiveAccount_returnsBadRequest() {
        InactiveAccountException ex = new InactiveAccountException("Inactive account");

        ResponseEntity<String> response = handler.handleInactiveAccount(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Inactive account", response.getBody());
    }

    @Test
    void handleInvalidInput_returnsBadRequest() {
        InvalidInputException ex = new InvalidInputException("Invalid input");

        ResponseEntity<String> response = handler.handleInvalidInput(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody());
    }



}