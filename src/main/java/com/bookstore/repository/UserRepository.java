package com.bookstore.repository;

import com.bookstore.repository.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

/**
 * Repository for the {@link Users} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    UserDetails findByUsername(String username);
}
