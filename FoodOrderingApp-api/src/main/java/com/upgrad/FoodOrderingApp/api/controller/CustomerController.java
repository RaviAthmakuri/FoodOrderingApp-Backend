package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;

import java.util.UUID;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST
            ,path = "/customer/signup"
            ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SignupCustomerResponse> createCustomer(
            @RequestBody final SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName((signupCustomerRequest.getLastName()));
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity savedCustomerEntity = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(savedCustomerEntity.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }
}
