package com.budlib.api.response;

import com.budlib.api.enums.*;
import org.springframework.http.HttpStatus;
import lombok.*;

/**
 * Wraps a custom message as object to be presented during authentication
 */
@Getter
@Setter
public class AuthResponse {
    /**
     * HTTP Status of the authentication
     */
    private HttpStatus status;

    /**
     * The message to provide explanation
     */
    private String message;

    /**
     * The username of the logged in user
     */
    private String username;

    /**
     * The role of the logged in user
     */
    private LibrarianRole role;

    /**
     * The id of the logged in user
     */
    private Long id;

    /**
     * The token generated after successful authentication
     */
    private String token;

    public AuthResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
        this.username = null;
        this.role = null;
        this.id = null;
        this.token = null;
    }

    public AuthResponse(HttpStatus httpStatus, String message, String username, LibrarianRole role, Long id,
            String token) {
        this.status = httpStatus;
        this.message = message;
        this.username = username;
        this.role = role;
        this.id = id;
        this.token = token;
    }
}
