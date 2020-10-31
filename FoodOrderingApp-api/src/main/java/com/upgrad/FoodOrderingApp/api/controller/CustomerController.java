package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;

import java.util.Base64;
import java.util.UUID;

@RestController
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @RequestMapping(method = RequestMethod.POST
            , path = "/customer/signup"
            , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SignupCustomerResponse> createCustomer(
            @RequestBody final SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {

        if (signupCustomerRequest.getFirstName().isEmpty() || signupCustomerRequest.getContactNumber().isEmpty()
                || signupCustomerRequest.getEmailAddress().isEmpty() || signupCustomerRequest.getPassword().isEmpty()) {

            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity savedCustomerEntity = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(savedCustomerEntity.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST
            , path = "/customer/login"
            , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<LoginResponse> customerLogin(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        String[] decodedArray;
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String deocodedText = new String(decode);
            decodedArray = deocodedText.split(":");
            if (decodedArray.length == 0) {
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
        } catch (Exception exe) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }


        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);

        LoginResponse loginResponse = new LoginResponse()
                .contactNumber(customerAuthEntity.getCustomerEntity().getContactNumber())
                .emailAddress(customerAuthEntity.getCustomerEntity().getEmail())
                .firstName(customerAuthEntity.getCustomerEntity().getFirstName())
                .lastName(customerAuthEntity.getCustomerEntity().getLastName())
                .id(customerAuthEntity.getCustomerEntity().getUuid())
                .message("LOGGED IN SUCCESSFULLY");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", customerAuthEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, httpHeaders, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST,
            path = "/customer/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") String userAccessToken)
            throws AuthorizationFailedException {
        String[] decodedArray = userAccessToken.split("Bearer ");
        CustomerAuthEntity customerAuthEntity = customerService.logout(decodedArray[1]);

        LogoutResponse logoutResponse = new LogoutResponse()
                .id(customerAuthEntity.getCustomerEntity().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.PUT,
            path = "/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(
            @RequestHeader("authorization") String userAccessToken,
            @RequestBody UpdateCustomerRequest updateCustomerRequest)
            throws AuthorizationFailedException, UpdateCustomerException {
        String[] decodedArray = userAccessToken.split("Bearer ");

        if(updateCustomerRequest.getFirstName() == null || updateCustomerRequest.getFirstName().isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        CustomerEntity customerEntity = customerService.getCustomer(decodedArray[1]);

        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updateCustomerEntity = customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .firstName(updateCustomerEntity.getFirstName())
                .lastName(updateCustomerEntity.getLastName())
                .id(updateCustomerEntity.getUuid())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            path = "/customer/password",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<UpdatePasswordResponse> updateCustomer(
            @RequestHeader("authorization") String userAccessToken,
            @RequestBody UpdatePasswordRequest updatePasswordRequest) throws UpdateCustomerException
            , AuthorizationFailedException {

                if(updatePasswordRequest.getNewPassword().isEmpty() || updatePasswordRequest.getOldPassword().isEmpty()){
                    throw new UpdateCustomerException("UCR-003","No field should be empty");
                }

                 String[] decodedArray = userAccessToken.split("Bearer ");
                 CustomerEntity customerEntity = customerService.getCustomer(decodedArray[1]);

        CustomerEntity updateCustomer = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), customerEntity);

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse,HttpStatus.OK);

    }

}
