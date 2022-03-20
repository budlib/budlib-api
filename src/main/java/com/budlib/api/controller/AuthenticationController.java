package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.response.*;
import com.budlib.api.security.*;
import com.budlib.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LibrarianService librarianService;

    @Autowired
    private Token token;

    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody Librarian l) {
        try {
            this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(l.getEmail(), l.getPassword()));
        }

        catch (BadCredentialsException e) {
            String message = "Incorrect email or password";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse(HttpStatus.FORBIDDEN, message));
        }

        catch (AuthenticationException e) {
            String message = "Incorrect email or password";
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
                .body(new AuthResponse(HttpStatus.OK, "", verifiedLibrarian.getUserName(),
                        verifiedLibrarian.getRole(), verifiedLibrarian.getLibrarianId(), jwt));
    }
}
