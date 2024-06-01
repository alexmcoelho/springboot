package com.grupoag.springboot.service;

import com.grupoag.springboot.domain.Customer;
import com.grupoag.springboot.exception.BadRequestException;
import com.grupoag.springboot.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Page<Customer> listAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public List<Customer> listAllNonPageable() {
        return customerRepository.findAll();
    }

    public Customer findByIdOrThrowBadRequestException(long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Customer not Found"));
    }

    @Transactional
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void delete(long id) {
        customerRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void update(Customer customer) {
        Customer savedCustomer = findByIdOrThrowBadRequestException(customer.getId());
        customer.setId(savedCustomer.getId());
        customerRepository.save(customer);
    }
}
