package com.copart.bankingApplication.repository;

import com.copart.bankingApplication.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountUuid(UUID accountUuid);
}
