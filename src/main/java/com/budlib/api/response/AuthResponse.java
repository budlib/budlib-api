package com.budlib.api.response;

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
     * The name of the logged in user
     */
    private String name;

    /**
     * The role of the logged in user
     */
    private String role;

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
        this.name = null;
        this.role = null;
        this.id = null;
        this.token = null;
    }

    public AuthResponse(HttpStatus httpStatus, String message, String name, String role, Long id, String token) {
        this.status = httpStatus;
        this.message = message;
        this.name = name;
        this.role = role;
        this.id = id;
        this.token = token;
    }
}
