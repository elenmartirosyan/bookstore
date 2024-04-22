package com.bookstore.controller;

import com.bookstore.repository.entity.Users;
import com.bookstore.service.TokenService;
import com.bookstore.service.dto.JwtDTO;
import com.bookstore.service.dto.SignInRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class with APIS for managing {@link Users} authentication.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtDTO> signIn(@RequestBody @Valid SignInRequestDTO signInRequestDTO) {
        final UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(signInRequestDTO.username(), signInRequestDTO.password());
        final Authentication authUser = authenticationManager.authenticate(usernamePassword);
        final String accessToken = tokenService.generateAccessToken((Users) authUser.getPrincipal());
        return ResponseEntity.ok(new JwtDTO(accessToken));
    }
}