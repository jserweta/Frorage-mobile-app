package com.frorage.server.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Class representing table product in database
 */
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name = "kitchen_id")
    private int kitchenId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "unit")
    private String unit;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "is_running_out", nullable = false)
    private Boolean runningOut = false;

    @Column(name = "favourite", nullable = false)
    private Boolean favourite = false;

    @Column(name = "to_buy", nullable = false)
    private Boolean toBuy = false;

    public Product(){};

    public Product(int kitchenId, String productName, Float amount, String unit, Date expirationDate) {
        this.kitchenId = kitchenId;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.runningOut = false;
        this.favourite = false;
        this.toBuy = false;

    }
    public Product(int kitchenId, String productName, Float amount, String unit, Date expirationDate, boolean runningOut, boolean toBuy, boolean favourite) {
        this.kitchenId = kitchenId;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.runningOut = runningOut;
        this.favourite = favourite;
        this.toBuy = toBuy;

    }
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(int kitchenId) {
        this.kitchenId = kitchenId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getRunningOut() {
        return runningOut;
    }

    public void setRunningOut(Boolean runningOut) {
        this.runningOut = runningOut;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getToBuy() {
        return toBuy;
    }

    public void setToBuy(Boolean toBuy) {
        this.toBuy = toBuy;
    }
}
