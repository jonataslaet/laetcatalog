package com.github.jonataslaet.laetcatalog.controllers.dtos;

public class UserInsertDTO extends UserDTO {

    private String password;

    public UserInsertDTO() {
        super();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
