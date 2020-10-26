package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerDao.getCustomerByContact(customerEntity.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");

        } else if (customerEntity.getFirstName() == null || customerEntity.getContactNumber() == null
                || customerEntity.getEmail() == null || customerEntity.getPassword() == null) {

            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");

        }else if(!isValidEmail(customerEntity.getEmail())){
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
//        }else if(!isValidMobile(customerEntity.getContactNumber())){
//            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }else{
            String password = customerEntity.getPassword();

            String[] encrypt = passwordCryptographyProvider.encrypt(password);
            customerEntity.setSalt(encrypt[0]);
            customerEntity.setPassword(encrypt[1]);

        }

        return customerDao.createCustomer(customerEntity);

    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public  boolean isValidMobile(String s)
    {

        Pattern p = Pattern.compile("[0-9][0-9]{9}");

        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
}
