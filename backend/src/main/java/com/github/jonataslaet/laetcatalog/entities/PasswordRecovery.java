package com.github.jonataslaet.laetcatalog.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_password_recovery")
public class PasswordRecovery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiration;

    @Column(nullable = false)
    private String email;

    public PasswordRecovery() {

    }

    public PasswordRecovery(Long id, String token, Instant expiration, String email) {
        this.id = id;
        this.token = token;
        this.expiration = expiration;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordRecovery role = (PasswordRecovery) o;
        return id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
