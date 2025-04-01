package com.copart.bankingApplication.repository;

import com.copart.bankingApplication.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
