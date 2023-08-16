package com.github.jonataslaet.laetcatalog.repositories;

import com.github.jonataslaet.laetcatalog.entities.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {

    @Query("SELECT passRecovery FROM PasswordRecovery passRecovery WHERE passRecovery.token = :token AND passRecovery.expiration > :now")
    List<PasswordRecovery> searchValidTokens(String token, Instant now);
}
