package com.upgrad.FoodOrderingApp.service.dao;

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

    public CustomerEntity getCustomerByContact(final String contactNumber){
        try{
            return entityManager.createNamedQuery("customerByContactNumber",CustomerEntity.class)
                        .setParameter("contactNumber",contactNumber)
                        .getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

    public CustomerEntity createCustomer(CustomerEntity customerEntity){
            entityManager.persist(customerEntity);
            return customerEntity;
    }
}
