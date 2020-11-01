package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    AddressDao addressDAO;


    public StateEntity getStateByUUID(String stateUuid) throws AddressNotFoundException {
        StateEntity stateEntity = addressDAO.getStateByUuid(stateUuid);
        if(stateEntity == null){
            throw new AddressNotFoundException("ANF-002","No state by this id");
        }else{
            return stateEntity;
        }

    }

    public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity)
            throws SaveAddressException {
        if(!isValidPinCode(addressEntity.getPincode())){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }
        List<CustomerEntity> customerEntities = new ArrayList<>();
        customerEntities.add(customerEntity);
        addressEntity.setCustomerEntityList(customerEntities);
        return addressDAO.saveAddress(addressEntity);

    }

    private  boolean isValidPinCode(String pinCode)
    {
        String regex = "^[0-9]{6}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pinCode);
        return m.matches();
    }

    public List<AddressEntity> getAllAddress() {
       return  addressDAO.getAllAddress();
    }
}
