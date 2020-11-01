package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name="address")
@NamedQueries({
        @NamedQuery(name="getAddress",query = "select a from AddressEntity a")
})
public class AddressEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 64)
    private String uuid;

    @Column(name="flat_build_number")
    @Size(max = 255)
    private String flatBuildNumber;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    @Size(max = 30)
    private String pincode;

    @Column(name = "active")
    @Size(max = 30)
    private Integer active;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateEntity stateEntity;

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

    public String getFlatBuildNumber() {
        return flatBuildNumber;
    }

    public void setFlatBuildNumber(String flatBuildNumber) {
        this.flatBuildNumber = flatBuildNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public StateEntity getStateEntity() {
        return stateEntity;
    }

    public void setStateEntity(StateEntity stateEntity) {
        this.stateEntity = stateEntity;
    }
}
