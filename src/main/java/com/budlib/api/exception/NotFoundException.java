package com.budlib.api.exception;

/**
 * Custom exception for cases when object is not found
 */
public class NotFoundException extends Exception {

    /**
     * Default UID for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param errorMessage the detailed error message
     */
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
