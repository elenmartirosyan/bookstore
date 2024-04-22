package com.bookstore.service;

import com.bookstore.repository.UserRepository;
import com.bookstore.repository.entity.Users;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service layer for the {@link Users} entity.
 */
@Service
public class AuthService implements UserDetailsService {

    private final UserRepository repository;

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull final String username) {
        return repository.findByUsername(username);
    }
}
