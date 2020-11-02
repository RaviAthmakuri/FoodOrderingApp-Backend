package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @RequestMapping(method = RequestMethod.POST
            , path = "/address"
            , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestHeader("authorization") final String authorization,
            @RequestBody SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        String[] decodedArray = authorization.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(decodedArray[1]);

        if (saveAddressRequest.getCity().isEmpty() || saveAddressRequest.getFlatBuildingName().isEmpty()
                || saveAddressRequest.getLocality().isEmpty() || saveAddressRequest.getPincode().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        StateEntity stateEntity = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setActive(1);
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setFlatBuildNumber(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setStateEntity(stateEntity);

        AddressEntity savedAddress = addressService.saveAddress(customerEntity, addressEntity);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(String.valueOf(addressEntity.getUuid()))
                .status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);


    }

    @RequestMapping(method = RequestMethod.GET
            , path = "/address/customer"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<AddressListResponse> getAddress(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        String[] decodedArray = authorization.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(decodedArray[1]);
        List<AddressEntity> addressEntityList = addressService.getAllAddress();

        Iterator<AddressEntity> entityIterator = addressEntityList.iterator();
        AddressListResponse addressListResponse = new AddressListResponse();
        while (entityIterator.hasNext()) {
            AddressEntity addressEntity = entityIterator.next();
            AddressList addressList = new AddressList();
            addressList.setCity(addressEntity.getCity());
            addressList.setFlatBuildingName(addressEntity.getFlatBuildNumber());
            addressList.setLocality(addressEntity.getLocality());
            addressList.setPincode(addressEntity.getPincode());
            AddressListState addressListState = new AddressListState()
                    .stateName(addressEntity.getStateEntity().getStateName())
                    .id(UUID.fromString(addressEntity.getStateEntity().getUuid()));
            addressList.setState(addressListState);
            addressList.setId(UUID.fromString(addressEntity.getUuid()));
            addressListResponse.addAddressesItem(addressList);
        }

        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);


    }


    @RequestMapping(method = RequestMethod.DELETE
            , path = "/address/{address_id}"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @RequestHeader("authorization") final String authorization
            , @PathVariable("address_id") String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        String[] decodedArray = authorization.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(decodedArray[1]);
        if (addressId == null) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity addressByUUID = addressService.getAddressByUUID(addressId,customerEntity);

        AddressEntity deletedAddress;
            deletedAddress = addressService.deleteAddress(addressByUUID);


        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);

    }


}
