package com.budlib.api.controller;

import java.time.ZonedDateTime;

import com.budlib.api.model.Librarian;
import com.budlib.api.response.AuthResponse;
import com.budlib.api.security.Token;
import com.budlib.api.service.LibrarianService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication
 */
@CrossOrigin
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final LibrarianService librarianService;
    private final Token token;

    @Autowired
    public AuthenticationController(
            final AuthenticationManager am,
            final LibrarianService ls,
            final Token t) {

        LOGGER.debug("AuthenticationController");

        this.authenticationManager = am;
        this.librarianService = ls;
        this.token = t;
    }

    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody Librarian l) {

        LOGGER.info("createAuthToken: librarian = {}", l);

        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(l.getEmail(), l.getPassword()));
        }

        catch (BadCredentialsException e) {
            String message = "Incorrect password";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse(HttpStatus.FORBIDDEN, message));
        }

        catch (AuthenticationException e) {
            String message = "Email not found";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse(HttpStatus.FORBIDDEN, message));
        }

        final UserDetails verifiedUser = this.librarianService.loadUserByUsername(l.getEmail());

        if (verifiedUser == null) {
            String message = "Incorrect email or password";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse(HttpStatus.FORBIDDEN, message));
        }

        final String jwt = this.token.generateToken(verifiedUser);

        Librarian verifiedLibrarian = this.librarianService.getLibrarianByEmail(l.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new AuthResponse(
                        HttpStatus.OK,
                        "",
                        verifiedLibrarian.getUserName(),
                        verifiedLibrarian.getRole(),
                        verifiedLibrarian.getLibrarianId(),
                        jwt,
                        ZonedDateTime.now().plusDays(7)));
    }
}
