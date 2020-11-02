package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.swing.plaf.nimbus.State;
import javax.validation.constraints.Size;

@Entity
@Table(name = "state")
@NamedQueries({
        @NamedQuery(name = "stateByUUID", query = "select s from StateEntity s where s.uuid = :uuid"),
        @NamedQuery(name = "getStates", query = "select s from StateEntity s")
})
public class StateEntity {


   public StateEntity() {
    }

    public StateEntity( String uuid,String name) {
        this.stateName = name;
        this.uuid = uuid;

    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 64)
    private String uuid;

    @Column(name = "state_name")
    @Size(max = 30)
    private String stateName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
