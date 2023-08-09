package com.github.jonataslaet.laetcatalog.repositories;

import com.github.jonataslaet.laetcatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
