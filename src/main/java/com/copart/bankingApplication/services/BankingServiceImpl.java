package com.copart.bankingApplication.services;

import com.copart.bankingApplication.dto.*;
import com.copart.bankingApplication.entity.Account;
import com.copart.bankingApplication.entity.Customer;
import com.copart.bankingApplication.exceptions.*;
import com.copart.bankingApplication.repository.AccountRepository;
import com.copart.bankingApplication.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BankingServiceImpl implements BankingService {
    private static final Logger logger = LoggerFactory.getLogger(BankingServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setAadharNumber(customerDTO.getAadharNumber());
        customer.setAddressLine1(customerDTO.getAddressLine1());
        customer.setAddressLine2(customerDTO.getAddressLine2());
        customer.setCity(customerDTO.getCity());
        customer.setState(customerDTO.getState());
        customer.setCountry(customerDTO.getCountry());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        try {
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Saving customer details { "+customer+" }");
            return mapToCustomerResponseDTO(savedCustomer);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.debug("Aadhaar number you entered already exists enter new aadhaar number");
            throw new InvalidInputException("Aadhaar number " + customerDTO.getAadharNumber() + " already exists");
        }
    }

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO accountDTO) {
        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + accountDTO.getCustomerId()));

        Account account = new Account();
        account.setCustomer(customer);
        account.setBalance(accountDTO.getInitialBalance() != null ? accountDTO.getInitialBalance() : BigDecimal.ZERO);


        Account savedAccount = accountRepository.save(account);
        logger.info("Creating an account for customer with id "+customer.getId()+" Account details are :"+savedAccount);
        return mapToAccountResponseDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO getAccountDetails(UUID accountUuid) {
        Account account = accountRepository.findByAccountUuid(accountUuid);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with UUID: " + accountUuid);
        }
        logger.info("Request for details of account with id "+accountUuid+" Customer is "+account.getCustomer());
        return mapToAccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO deposit(UUID accountUuid, TransactionRequestDTO transactionDTO) {
        BigDecimal amount=transactionDTO.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByAccountUuid(accountUuid);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with UUID: " + accountUuid);
        }
        if (account.getStatus() == Account.AccountStatus.INACTIVE) {
            throw new InactiveAccountException("Cannot deposit to inactive account");
        }

        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);
        return mapToAccountResponseDTO(updatedAccount);
    }

    @Override
    public AccountResponseDTO withdraw(UUID accountUuid, TransactionRequestDTO transactionDTO) {
        BigDecimal amount=transactionDTO.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findByAccountUuid(accountUuid);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with UUID: " + accountUuid);
        }
        if (account.getStatus() == Account.AccountStatus.INACTIVE) {
            throw new InactiveAccountException("Cannot withdraw from inactive account");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepository.save(account);
        return mapToAccountResponseDTO(updatedAccount);
    }

    @Override
    public void inactivateAccount(UUID accountUuid) {
        Account account = accountRepository.findByAccountUuid(accountUuid);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with UUID: " + accountUuid);
        }
        logger.info("Request to Inactivation of account :"+accountUuid);
        account.setStatus(Account.AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    @Override
    public void activateAccount(UUID accountUuid) {
        Account account = accountRepository.findByAccountUuid(accountUuid);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with UUID: " + accountUuid);
        }
        logger.info("Request to activation of account :"+accountUuid);
        account.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Override
    public BalanceResponseDTO checkBalance(UUID accounUuid){
        Account account=accountRepository.findByAccountUuid(accounUuid);
        if(account == null){
            throw new AccountNotFoundException("Account not found with UUID: "+accounUuid);
        }
        logger.info("Request to check balance for account :"+accounUuid+" this account belongs to "+account.getCustomer());
        BigDecimal amount=account.getBalance();
        return mapToBalanceResponseDTO(amount);
    }

    private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setAadharNumber(customer.getAadharNumber());
        dto.setAddressLine1(customer.getAddressLine1());
        dto.setAddressLine2(customer.getAddressLine2());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setCountry(customer.getCountry());
        dto.setPhoneNumber(customer.getPhoneNumber());
        return dto;
    }

    private AccountResponseDTO mapToAccountResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setAccountUuid(account.getAccountUuid());
        dto.setCustomerId(account.getCustomer().getId());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus().name());
        return dto;
    }

    private BalanceResponseDTO mapToBalanceResponseDTO(BigDecimal amount){
        BalanceResponseDTO dto=new BalanceResponseDTO();
        dto.setAmount(amount);
        return dto;
    }
}
