package org.example.taskFlow.repository;

import org.example.taskFlow.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    Optional<Password> findByUserId(long userId);
}
