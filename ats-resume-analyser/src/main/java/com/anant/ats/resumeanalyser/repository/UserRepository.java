package com.anant.ats.resumeanalyser.repository;

import com.anant.ats.resumeanalyser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ email se user find (login / register ke liye)
    Optional<User> findByEmail(String email);

    // ✅ username se user find (Spring Security ke liye)
    Optional<User> findByUsername(String username);
}