package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if(findCustomer(customerEntity.getContactNumber()) != null){
            throw new SignUpRestrictedException("ATH-001","This contact number has not been registered");
        }else if (!isValidEmail(customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        } else if (!isValidMobile(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        } else if (!isValidPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        } else {
            String password = customerEntity.getPassword();

            String[] encrypt = passwordCryptographyProvider.encrypt(password);
            customerEntity.setSalt(encrypt[0]);
            customerEntity.setPassword(encrypt[1]);

        }

        return customerDao.createCustomer(customerEntity);

    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean isValidMobile(String s) {

        Pattern p = Pattern.compile("[0-9][0-9]{10}");

        Matcher m = p.matcher(s);
        return (m.find());
    }

    private static boolean
    isValidPassword(String password) {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);


        Matcher m = p.matcher(password);


        return m.matches();
    }

    private CustomerEntity findCustomer(String contactNumber) {
        return customerDao.getCustomerByContact(contactNumber);
    }

    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {
        CustomerEntity customerEntity = findCustomer(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setUuid(customerEntity.getUuid());
            customerAuthEntity.setCustomer(customerEntity);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));

            return customerDao.saveCustomerAuth(customerAuthEntity);
//                return customerEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

    }

    public CustomerAuthEntity logout(String userAccessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getUserByToken(userAccessToken);
        ZonedDateTime now = ZonedDateTime.now();
        if(customerAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        }else if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","Customer is loggedout.Log in again to access this endpoint.");
        }else if(customerAuthEntity.getExpiresAt().compareTo(now) > 0){
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }

        customerAuthEntity.setLogoutAt(now);
        customerDao.updateCustomerAuth(customerAuthEntity);
        return customerAuthEntity;

    }

    public CustomerEntity getCustomer(String s) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getUserByToken(s);
        ZonedDateTime now = ZonedDateTime.now();
        if(customerAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        }else if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","Customer is loggedout.Log in again to access this endpoint.");
        }else if(customerAuthEntity.getExpiresAt().compareTo(now) > 0){
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthEntity.getCustomerEntity();
    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) throws UpdateCustomerException {
            return customerDao.updateCustomer(customerEntity);
    }

    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customerEntity)
            throws UpdateCustomerException {

        if(!isValidPassword(newPassword)){
            throw new UpdateCustomerException("UCR-001","Weak password!");
        }

        String encryptedPassword = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());

        if(encryptedPassword.equals(customerEntity.getPassword())){

            String[] encrypt = passwordCryptographyProvider.encrypt(newPassword);
            customerEntity.setSalt(encrypt[0]);
            customerEntity.setPassword(encrypt[1]);
           return customerDao.updateCustomer(customerEntity);
        }else{
            throw new UpdateCustomerException("UCR=-004","Incorrect old password!");
        }

    }
}
