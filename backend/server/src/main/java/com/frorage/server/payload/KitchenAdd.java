package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;

public class KitchenAdd {

    @NotBlank
    private String kitchenName;

    @NotBlank
    private String kitchenPassword;

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
        this.kitchenPassword = kitchenPassword;
    }


    public KitchenAdd(String kitchenName, String kitchenPassword){
        this.kitchenName = kitchenName;
        this.kitchenPassword = kitchenPassword;
    }
}
