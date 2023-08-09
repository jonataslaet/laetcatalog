package com.github.jonataslaet.laetcatalog.controllers.dtos;

import com.github.jonataslaet.laetcatalog.entities.Role;
import com.github.jonataslaet.laetcatalog.entities.User;

import java.util.HashSet;

public class RoleDTO {

    private Long id;
    private String authority;

    public RoleDTO() {
    }

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
