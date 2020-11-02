package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.StatesList;
import com.upgrad.FoodOrderingApp.api.model.StatesListResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class StateController {

    @Autowired
    AddressService addressService;

    @RequestMapping(method = RequestMethod.GET
            , path = "/states"
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<StatesListResponse> getStates(){
        List<StateEntity> stateEntities = addressService.getAllStates();

//        List<StatesList> statesLists = new ArrayList<>();
        StatesListResponse statesListResponse = new StatesListResponse();
        stateEntities.forEach(stateEntity -> {
            StatesList statesList = new StatesList()
                                .id(UUID.fromString(stateEntity.getUuid()))
                                .stateName(stateEntity.getStateName());
            statesListResponse.addStatesItem(statesList);
        });

        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);

    }
}
