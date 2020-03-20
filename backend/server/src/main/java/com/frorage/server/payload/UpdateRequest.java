package com.frorage.server.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateRequest {

    @NotBlank
    @Size(min = 6, max = 20)
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 20)
    private String oldPassword;


    public UpdateRequest(){
    }

    public UpdateRequest(@NotBlank @Size(min = 6, max = 20) String newPassword, @NotBlank @Size(min = 6, max = 20) String oldPassword) {
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
