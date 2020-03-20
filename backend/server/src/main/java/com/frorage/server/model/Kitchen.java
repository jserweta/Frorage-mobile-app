package com.frorage.server.model;

import org.springframework.util.DigestUtils;

import javax.persistence.*;

/**
 * Class representing table kitchen in database
 */
@Entity
@Table(name = "kitchen")
public class Kitchen {

    @Id
    @Column(name = "kitchen_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int kitchenId;

    @Column(name = "kitchen_name")
    private String kitchenName;

    @Column(name = "kitchen_password")
    private String kitchenPassword;

    public Kitchen(){}

    public Kitchen(String kitchenName, String kitchenPassword){
        this.kitchenName = kitchenName;
        this.kitchenPassword = DigestUtils.md5DigestAsHex(kitchenPassword.getBytes());
    }

    public Kitchen(int kitchenId, String kitchenName, String kitchenPassword){
        this.kitchenId = kitchenId;
        this.kitchenName = kitchenName;
        this.kitchenPassword =  DigestUtils.md5DigestAsHex(kitchenPassword.getBytes());
    }

    public int getKitchenId() {
        return kitchenId;
    }

    public String getKitchenName() {
        return kitchenName;
    }

    public void setKitchenName(String kitchenName) {
        this.kitchenName = kitchenName;
    }

    public String getKitchenPassword() {
        return kitchenPassword;
    }

    public void setKitchenPassword(String kitchenPassword) {
        this.kitchenPassword =  DigestUtils.md5DigestAsHex(kitchenPassword.getBytes());
    }

    public void setKitchenId(int kitchenId) { this.kitchenId = kitchenId;}
//    public String toString() {
//        return "Kitchen{" +
//                "kitchenId=" + kitchenId +
//                ", kitchenName='" + kitchenName + '\'' +
//                ", kitchenPassword='" + kitchenPassword + '\'' +
//                '}';
//    }
}
