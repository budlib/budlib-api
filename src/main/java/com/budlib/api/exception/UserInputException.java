package com.budlib.api.exception;

/**
 * Custom exception for cases when user input is unexpected
 */
public class UserInputException extends Exception {

    /**
     * Default UID for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param errorMessage the detailed error message
     */
    public UserInputException(String errorMessage) {
        super(errorMessage);
    }
}
