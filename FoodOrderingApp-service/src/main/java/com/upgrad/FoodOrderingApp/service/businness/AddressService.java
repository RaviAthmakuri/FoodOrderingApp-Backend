package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    AddressDao addressDAO;

    @Autowired
    CustomerAddressDao customerAddressDao;


    public StateEntity getStateByUUID(String stateUuid) throws AddressNotFoundException {
        StateEntity stateEntity = addressDAO.getStateByUuid(stateUuid);
        if (stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            return stateEntity;
        }

    }

//    public AddressEntity getAddressByUUID(String addressUuid,CustomerEntity customerEntity)throws AuthorizationFailedException,AddressNotFoundException{
//        if(addressUuid == null){
//            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
//        }
//        AddressEntity addressEntity = addressDAO.getAddressByUuid(addressUuid);
//        if (addressEntity == null){//Checking if null throws corresponding exception.
//            throw new AddressNotFoundException("ANF-003","No address by this id");
//        }
//
//        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByAddress(addressEntity);
//
//        //Checking if the address belong to the customer requested.If no throws corresponding exception.
//        if(customerAddressEntity.getCustomer().getUuid() == customerEntity.getUuid()){
//            return addressEntity;
//        }else{
//            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");
//        }
//
//    }

    public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity)
            throws SaveAddressException {
        if (!isValidPinCode(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        List<CustomerEntity> customerEntities = new ArrayList<>();
        customerEntities.add(customerEntity);
        addressEntity.setCustomerEntityList(customerEntities);
        return addressDAO.saveAddress(addressEntity);

    }

    private boolean isValidPinCode(String pinCode) {
        String regex = "^[0-9]{6}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pinCode);
        return m.matches();
    }

    public List<AddressEntity> getAllAddress() {
        return addressDAO.getAllAddress();
    }

    public AddressEntity getAddressByUUID(String addressId, CustomerEntity customerEntity) throws AddressNotFoundException, AuthorizationFailedException {
        AddressEntity addressByUUID = addressDAO.getAddressByUUID(addressId);

        if (addressByUUID == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        } else {
            Iterator entityIterator = addressByUUID.getCustomerEntityList().iterator();
            boolean checkCustomer = false;
            while (entityIterator.hasNext()) {
                CustomerEntity addressCustomer = (CustomerEntity) entityIterator.next();
                if (addressCustomer.getUuid().equals(customerEntity.getUuid())) {
                    checkCustomer = true;
                    break;
                }
            }

            if (!checkCustomer) {
                throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
            }
            return addressByUUID;
        }


    }

    public AddressEntity deleteAddress(AddressEntity addressByUUID) {

        addressDAO.deleteAddress(addressByUUID);

        return addressByUUID;

    }


}
