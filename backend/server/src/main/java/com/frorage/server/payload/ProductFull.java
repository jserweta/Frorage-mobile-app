package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class ProductFull {


        private int kitchenId;

        @NotBlank
        private String productName;

        @NotBlank
        private Float amount;

        @NotBlank
        private String unit;

        private Date expirationDate;

        @NotBlank
        private Boolean runningOut = false;

        @NotBlank
        private Boolean favourite = false;

        @NotBlank
        private Boolean toBuy = false;

    public ProductFull(int kitchenId, @NotBlank String productName, @NotBlank Float amount, @NotBlank String unit, Date expirationDate, @NotBlank Boolean runningOut, @NotBlank Boolean favourite, @NotBlank Boolean toBuy) {
        this.kitchenId = kitchenId;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.runningOut = runningOut;
        this.favourite = favourite;
        this.toBuy = toBuy;
    }

    public ProductFull() {
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


