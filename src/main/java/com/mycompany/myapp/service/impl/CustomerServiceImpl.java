package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.service.CustomerService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Customer}.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        LOG.debug("Request to save Customer : {}", customer);
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Customer customer) {
        LOG.debug("Request to update Customer : {}", customer);
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> partialUpdate(Customer customer) {
        LOG.debug("Request to partially update Customer : {}", customer);

        return customerRepository
            .findById(customer.getId())
            .map(existingCustomer -> {
                if (customer.getFullName() != null) {
                    existingCustomer.setFullName(customer.getFullName());
                }
                if (customer.getPhoneNumber() != null) {
                    existingCustomer.setPhoneNumber(customer.getPhoneNumber());
                }

                return existingCustomer;
            })
            .map(customerRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        LOG.debug("Request to get all Customers");
        return customerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findOne(Long id) {
        LOG.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
    }
}
