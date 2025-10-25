package com.example.busconnect.domine.repositories;

import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.UserRole;
import com.example.busconnect.domine.entities.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByRole(UserRole role);

    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
}
