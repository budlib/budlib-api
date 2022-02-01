package com.budlib.api.model;

import org.springframework.http.HttpStatus;

/**
 * Wraps a custom message as object to be presented during bad requests
 */
public class ErrorBody {
    private HttpStatus status;
    private String message;

    /**
     * Constructor
     *
     * @param code    the bad http status code
     * @param message the message to provide explanation
     */
    public ErrorBody(HttpStatus code, String message) {
        this.status = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
