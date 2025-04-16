package com.Copart.bankingApplication.service;

import com.copart.bankingApplication.dto.*;
import com.copart.bankingApplication.exceptions.*;
import com.copart.bankingApplication.repository.AccountRepository;
import com.copart.bankingApplication.repository.CustomerRepository;
import com.copart.bankingApplication.services.BankingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.copart.bankingApplication.entity.Account;
import com.copart.bankingApplication.entity.Customer;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankingServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingServiceImpl bankingService;

    private Customer customer;
    private Account account;
    private CustomerRequestDTO customerRequestDTO;
    private AccountRequestDTO accountRequestDTO;
    private TransactionRequestDTO transactionRequestDTO;
    private UUID accountUuid;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("John Doe");
        customer.setAadharNumber("234567890123");

        accountUuid = UUID.randomUUID();
        account = new Account();
        account.setId(1L);
        account.setAccountUuid(accountUuid);
        account.setCustomer(customer);
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(Account.AccountStatus.ACTIVE);

        customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerName("John Doe");
        customerRequestDTO.setAadharNumber("234567890123");
        customerRequestDTO.setAddressLine1("123 Main St");
        customerRequestDTO.setCity("Mumbai");
        customerRequestDTO.setState("Maharashtra");
        customerRequestDTO.setCountry("India");
        customerRequestDTO.setPhoneNumber("919876543210");

        accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setCustomerId(1L);
        accountRequestDTO.setInitialBalance(new BigDecimal("1000.00"));

        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void testCreateAccount_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO response = bankingService.createCustomer(customerRequestDTO);


        assertNotNull(response);
        assertEquals(1L,response.getId());
        assertEquals("John Doe",response.getCustomerName());

        verify(customerRepository).save(any(Customer.class));

    }

    @Test
    void testCreateAccount_Failure() {
        when(customerRepository.save(any(Customer.class))).thenThrow(new DataIntegrityViolationException("Aadhaar number already exists"));

        InvalidInputException exception= assertThrows(InvalidInputException.class, () -> bankingService.createCustomer(customerRequestDTO));
        assertEquals("Aadhaar number 234567890123 already exists",exception.getMessage());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateAccount_Success_With_Initial_Balance() {
        when(customerRepository.findById(any(long.class))).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        AccountResponseDTO savedaccount= bankingService.createAccount(accountRequestDTO);

        assertNotNull(savedaccount);
        assertEquals(1L,savedaccount.getId());
        assertEquals(accountUuid,savedaccount.getAccountUuid());
        assertEquals(1L,savedaccount.getCustomerId());
        assertEquals(new BigDecimal("1000.00"),savedaccount.getBalance());
        assertEquals("ACTIVE",savedaccount.getStatus());
        verify(customerRepository).findById(any(long.class));
        verify(accountRepository).save(any(Account.class));


    }

    @Test
    void testCreateAccount_Failure_With_Initial_Balance() {
        when(customerRepository.findById(any(long.class))).thenReturn(Optional.empty());
        CustomerNotFoundException exception= assertThrows(CustomerNotFoundException.class, () -> bankingService.createAccount(accountRequestDTO));
        assertEquals("Customer not found with id: 1",exception.getMessage());
        verify(customerRepository).findById(any(long.class));
    }
    @Test
    void getAccountDetails_Success() {
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);
        AccountResponseDTO response=bankingService.getAccountDetails(accountUuid);
        assertNotNull(response);
        assertEquals(1L,response.getId());
        assertEquals(accountUuid,response.getAccountUuid());
        assertEquals(1L,response.getCustomerId());
        assertEquals(new BigDecimal("1000.00"),response.getBalance());
        assertEquals("ACTIVE",response.getStatus());
        verify(accountRepository).findByAccountUuid(accountUuid);

    }
    @Test
    void getAccountDetails_accountNotFound_throwsAccountNotFoundException() {
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(null);

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.getAccountDetails(accountUuid)
        );

        assertEquals("Account not found with UUID: " + accountUuid, exception.getMessage());
        verify(accountRepository).findByAccountUuid(accountUuid);
    }
    @Test
    void deposit_Success() {
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        AccountResponseDTO response=bankingService.deposit(accountUuid,transactionRequestDTO);
        assertNotNull(response);
        assertEquals(1L,response.getId());
        assertEquals(accountUuid,response.getAccountUuid());
        assertEquals(1L,response.getCustomerId());
        assertEquals(new BigDecimal("1500.00"),response.getBalance());
    }
    @Test
    void deposit_invalidAmount_throwsInvalidAmountException() {
        transactionRequestDTO.setAmount(new BigDecimal("0.00"));

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(accountUuid, transactionRequestDTO)
        );

        assertEquals("Deposit amount must be positive", exception.getMessage());
        verify(accountRepository, never()).findByAccountUuid(any(UUID.class));
    }

    @Test
    void deposit_inactiveAccount_throwsInactiveAccountException() {
        account.setStatus(Account.AccountStatus.INACTIVE);
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);

        InactiveAccountException exception = assertThrows(
                InactiveAccountException.class,
                () -> bankingService.deposit(accountUuid, transactionRequestDTO)
        );

        assertEquals("Cannot deposit to inactive account", exception.getMessage());
        verify(accountRepository).findByAccountUuid(accountUuid);
        verify(accountRepository, never()).save(any(Account.class));
    }
    @Test
    void withdraw_success() {
        transactionRequestDTO.setAmount(new BigDecimal("200.00"));
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDTO response = bankingService.withdraw(accountUuid, transactionRequestDTO);

        assertNotNull(response);
        assertEquals(new BigDecimal("800.00"), response.getBalance());
        verify(accountRepository).findByAccountUuid(accountUuid);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void withdraw_insufficientBalance_throwsInsufficientBalanceException() {
        transactionRequestDTO.setAmount(new BigDecimal("2000.00"));
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> bankingService.withdraw(accountUuid, transactionRequestDTO)
        );

        assertEquals("Insufficient balance for withdrawal", exception.getMessage());
        verify(accountRepository).findByAccountUuid(accountUuid);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void inactivateAccount_success() {
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);

        bankingService.inactivateAccount(accountUuid);

        verify(accountRepository).findByAccountUuid(accountUuid);
        verify(accountRepository).save(account);
        assertEquals(Account.AccountStatus.INACTIVE, account.getStatus());
    }

    @Test
    void activateAccount_success() {
        account.setStatus(Account.AccountStatus.INACTIVE);
        when(accountRepository.findByAccountUuid(accountUuid)).thenReturn(account);

        bankingService.activateAccount(accountUuid);

        verify(accountRepository).findByAccountUuid(accountUuid);
        verify(accountRepository).save(account);
        assertEquals(Account.AccountStatus.ACTIVE, account.getStatus());
    }
}













