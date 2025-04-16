package com.Copart.bankingApplication.controller;

import com.copart.bankingApplication.controller.BankingController;
import com.copart.bankingApplication.dto.*;
import com.copart.bankingApplication.services.BankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BankingService bankingService;

    @InjectMocks
    private BankingController bankingController;

    private ObjectMapper objectMapper;

    private CustomerRequestDTO customerRequestDTO;
    private CustomerResponseDTO customerResponseDTO;
    private AccountRequestDTO accountRequestDTO;
    private AccountResponseDTO accountResponseDTO;
    private TransactionRequestDTO transactionRequestDTO;
    private UUID accountUuid;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bankingController).build();

        accountUuid = UUID.randomUUID();

        customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerName("John Doe");
        customerRequestDTO.setAadharNumber("234567890123");
        customerRequestDTO.setAddressLine1("123 Main St");
        customerRequestDTO.setCity("Mumbai");
        customerRequestDTO.setState("Maharashtra");
        customerRequestDTO.setCountry("India");
        customerRequestDTO.setPhoneNumber("919876543210");

        customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setId(1L);
        customerResponseDTO.setCustomerName("John Doe");

        accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setCustomerId(1L);
        accountRequestDTO.setInitialBalance(new BigDecimal("1000.00"));

        accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setId(1L);
        accountResponseDTO.setAccountUuid(accountUuid);
        accountResponseDTO.setCustomerId(1L);
        accountResponseDTO.setBalance(new BigDecimal("1000.00"));
        accountResponseDTO.setStatus("ACTIVE");

        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void createCustomer_success() throws Exception {
        when(bankingService.createCustomer(any(CustomerRequestDTO.class)))
                .thenReturn(customerResponseDTO);

        mockMvc.perform(post("/api/banking/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(bankingService).createCustomer(any(CustomerRequestDTO.class));
    }

    @Test
    void createAccount_success() throws Exception {
        when(bankingService.createAccount(any(AccountRequestDTO.class)))
                .thenReturn(accountResponseDTO);

        mockMvc.perform(post("/api/banking/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountUuid").value(accountUuid.toString()))
                .andExpect(jsonPath("$.balance").value(1000.00));

        verify(bankingService).createAccount(any(AccountRequestDTO.class));
    }

    @Test
    void getAccountDetails_success() throws Exception {
        when(bankingService.getAccountDetails(accountUuid)).thenReturn(accountResponseDTO);

        mockMvc.perform(get("/api/banking/accounts/{accountUuid}", accountUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountUuid").value(accountUuid.toString()))
                .andExpect(jsonPath("$.balance").value(1000.00));

        verify(bankingService).getAccountDetails(accountUuid);
    }

    @Test
    void deposit_success() throws Exception {
        accountResponseDTO.setBalance(new BigDecimal("1500.00"));
        when(bankingService.deposit(any(UUID.class), any(TransactionRequestDTO.class)))
                .thenReturn(accountResponseDTO);

        mockMvc.perform(post("/api/banking/accounts/{accountUuid}/deposit", accountUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.00));

        verify(bankingService).deposit(eq(accountUuid), any(TransactionRequestDTO.class));
    }

    @Test
    void withdraw_success() throws Exception {
        accountResponseDTO.setBalance(new BigDecimal("500.00"));
        when(bankingService.withdraw(any(UUID.class), any(TransactionRequestDTO.class)))
                .thenReturn(accountResponseDTO);

        mockMvc.perform(post("/api/banking/accounts/{accountUuid}/withdraw", accountUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.00));

        verify(bankingService).withdraw(eq(accountUuid), any(TransactionRequestDTO.class));
    }

    @Test
    void inactivateAccount_success() throws Exception {
        mockMvc.perform(put("/api/banking/accounts/{accountUuid}/inactivate", accountUuid))
                .andExpect(status().isOk())
                .andExpect(content().string("Account inactivated successfully"));

        verify(bankingService).inactivateAccount(accountUuid);
    }

    @Test
    void activateAccount_success() throws Exception {
        mockMvc.perform(put("/api/banking/accounts/{accountUuid}/activate", accountUuid))
                .andExpect(status().isOk())
                .andExpect(content().string("Account activated successfully"));

        verify(bankingService).activateAccount(accountUuid);
    }
}