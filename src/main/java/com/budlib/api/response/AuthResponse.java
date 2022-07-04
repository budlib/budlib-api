package com.budlib.api.response;

import java.time.ZonedDateTime;

import com.budlib.api.enums.LibrarianRole;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

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

    /**
     * Expiry date time of the token
     */
    private ZonedDateTime expiry;

    /**
     * Constructor for creating failed authorization responses
     *
     * @param httpStatus HTTP status of the type of failure
     * @param message failure cause
     */
    public AuthResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
        this.username = null;
        this.role = null;
        this.id = null;
        this.token = null;
        this.expiry = null;
    }

    /**
     * Constructor for creating successful authorization responses
     *
     * @param httpStatus HTTP status of the authorization request
     * @param message success message, if any
     * @param username username of the authenticated email
     * @param role {@link LibrarianRole} of the authenticated email
     * @param id ID of the authenticated email
     * @param token JWT token
     * @param expiry expiry date of the token
     */
    public AuthResponse(HttpStatus httpStatus, String message, String username, LibrarianRole role, Long id,
            String token, ZonedDateTime expiry) {
        this.status = httpStatus;
        this.message = message;
        this.username = username;
        this.role = role;
        this.id = id;
        this.token = token;
        this.expiry = expiry;
    }
}
