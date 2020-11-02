package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity getCustomerByContact(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerAuthEntity saveCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerAuthEntity getUserByToken(String userAccessToken) {
        try {
            CustomerAuthEntity customerAuthEntity = entityManager
                    .createNamedQuery("customerByToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", userAccessToken)
                    .getSingleResult();
            return customerAuthEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public CustomerAuthEntity updateCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;

    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;

    }

    public CustomerEntity getCustomerByUuid (final String uuid){
        try {
            CustomerEntity customer = entityManager.createNamedQuery("customerByUuid",CustomerEntity.class)
                    .setParameter("uuid",uuid).getSingleResult();
            return customer;
        }catch (NoResultException nre){
            return null;
        }
    }
}
