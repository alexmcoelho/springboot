package com.grupoag.springboot.repository;

import com.grupoag.springboot.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
