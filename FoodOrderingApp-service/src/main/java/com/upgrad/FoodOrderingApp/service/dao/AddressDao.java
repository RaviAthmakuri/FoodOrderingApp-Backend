package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class AddressDao {

    @PersistenceContext
    EntityManager entityManager;

    public StateEntity getStateByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("stateByUUID", StateEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public AddressEntity saveAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public AddressEntity getAddressByUuid(String uuid){
        try{
            AddressEntity addressEntity = entityManager.createNamedQuery("getAddressByUuid",AddressEntity.class).setParameter("uuid",uuid).getSingleResult();
            return addressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public List<AddressEntity> getAllAddress() {
        try {
            List<AddressEntity> addressList = entityManager.createNamedQuery("getAddress", AddressEntity.class)
                    .getResultList();
            return addressList;
        } catch (NoResultException nre) {
            return null;
        }


    }
}
