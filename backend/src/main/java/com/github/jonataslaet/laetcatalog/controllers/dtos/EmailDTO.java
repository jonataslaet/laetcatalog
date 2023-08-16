package com.github.jonataslaet.laetcatalog.controllers.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {

    @NotBlank(message = "Email must be not empty")
    @Email(message = "Email must be valid")
    private String email;

    public EmailDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
