package com.github.jonataslaet.laetcatalog.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public class NewPasswordDTO {

    @NotBlank(message = "Password must be not empty")
    private String newPassword;

    private String newPasswordConfirmation;

    public NewPasswordDTO() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }
}
