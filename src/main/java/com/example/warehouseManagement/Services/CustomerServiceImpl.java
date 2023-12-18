package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Exceptions.CustomerNotFoundException;
import com.example.warehouseManagement.Repositories.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    
    
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Iterable<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer updateById(Long id, Customer customer) throws CustomerNotFoundException {
        if (customerRepository.findById(id).isEmpty()) {
            throw new CustomerNotFoundException();
        } else {
            Customer existing = customerRepository.findById(id).get();
            //Updates fields with new values
            existing.setName(customer.getName());
            existing.setStreet(customer.getStreet());
            existing.setCity(customer.getCity());
            existing.setZipcode(customer.getZipcode());
            existing.setState(customer.getState());
            existing.setContactInfo(customer.getContactInfo());
            return customerRepository.save(existing);
        }
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
    
}
