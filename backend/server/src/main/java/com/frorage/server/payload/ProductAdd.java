package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class ProductAdd {

    private int kitchenId;

    @NotBlank
    private String productName;

    @NotBlank
    private Float amount;

    @NotBlank
    private String unit;

    private Date expirationDate;

    public ProductAdd(int kitchenId, String productName, Float amount, String unit, Date expirationDate){
        this.kitchenId = kitchenId;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
        this.expirationDate = expirationDate;
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
}
