package com.budlib.api.response;

import org.springframework.http.HttpStatus;
import lombok.*;

/**
 * Wraps a custom message as object to be presented during bad requests
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorBody {
    /**
     * HTTP Status of the bad request
     */
    private HttpStatus status;

    /**
     * The message to provide explanation
     */
    private String message;
}
