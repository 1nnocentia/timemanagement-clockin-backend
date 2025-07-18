package com.clockin.clockin.repository;

import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetPasswordToken(String resetPasswordToken);
    Optional<User> findByUsernameIgnoreCase(String username);
}