package com.budlib.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Librarian;
import com.budlib.api.model.Transaction;
import com.budlib.api.response.ErrorBody;
import com.budlib.api.service.LibrarianService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Librarian
 */
@CrossOrigin
@RestController
@RequestMapping("api/librarian")
public class LibrarianController {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LibrarianController.class);

    /**
     * {@link LibrarianService} for interacting with Librarians
     */
    private final LibrarianService librarianService;

    /**
     * Constructor
     *
     * @param ls {@code LibrarianService} for interaction with Librarians
     */
    @Autowired
    public LibrarianController(final LibrarianService ls) {

        LOGGER.debug("LibrarianController: injected LibrarianService");

        this.librarianService = ls;
    }

    /**
     * Get {@link Librarian} by ID
     *
     * @param librarianId ID of the Librarian
     * @return Librarian
     */
    @GetMapping(path = "{librarianId}")
    public ResponseEntity<?> getLibrarianById(final @PathVariable("librarianId") Long librarianId) {

        LOGGER.info("getLibrarianById: librarianId = {}", librarianId);

        Librarian l = this.librarianService.getLibrarianById(librarianId);

        if (l == null) {
            String message = "Librarian not found";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(l);
        }
    }

    /**
     * Get list of {@link Transaction} coordinated by the {@link Librarian}
     *
     * @param librarianId Librarian ID whose coordinated history is required
     * @return list of Transactions coordinated by the Librarian
     */
    @GetMapping(path = "{librarianId}/history")
    public ResponseEntity<?> getCoordinationHistory(final @PathVariable("librarianId") Long librarianId) {

        LOGGER.info("getCoordinationHistory: librarianId = {}", librarianId);

        try {
            List<Transaction> trnList = this.librarianService.getCoordinationHistory(librarianId);
            return ResponseEntity.status(HttpStatus.OK).body(trnList);
        }

        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Search {@link Librarian} in the database
     *
     * @param email    email of the Librarian to filter
     * @param userName username of the Librarian to filter
     * @param name     name of the Librarian to filter
     * @return list of Librarians that match the filters
     */
    @GetMapping()
    public ResponseEntity<?> searchLibrarian(final @RequestParam(name = "email", required = false) String email,
            final @RequestParam(name = "username", required = false) String userName,
            final @RequestParam(name = "name", required = false) String name) {

        StringBuilder sb = new StringBuilder();
        sb.append("email = ").append(email).append(", ");
        sb.append("username = ").append(userName).append(", ");
        sb.append("name = ").append(name).append(", ");

        LOGGER.info("searchLibrarian: parameters = {}", sb.toString());

        Map<String, String> parameters = new HashMap<>();

        if (email != null) {
            parameters.put("email", email);
        }

        if (userName != null) {
            parameters.put("username", userName);
        }

        if (name != null) {
            parameters.put("name", name);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.librarianService.searchLibrarian(parameters));
    }

    /**
     * Save the {@link Librarian} in the database
     *
     * @param librarian Librarian details
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addLibrarian(final @RequestBody Librarian librarian) {

        LOGGER.info("addLibrarian: librarian = {}", librarian);

        try {
            this.librarianService.addLibrarian(librarian);

            String message = "Librarian added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Update the {@link Librarian} in the database
     *
     * @param librarian   Librarian details
     * @param librarianId ID of the Librarian to be updated
     * @return the message
     */
    @PutMapping(path = "{librarianId}")
    public ResponseEntity<?> updateLibrarian(final @RequestBody Librarian l,
            final @PathVariable("librarianId") Long librarianId) {

        LOGGER.info("updateLibrarian: librarian = {}, librarianId = {}", l, librarianId);

        try {
            this.librarianService.updateLibrarian(l, librarianId);

            String message = "Librarian updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Update the password of the {@link Librarian} in the database
     *
     * @param librarian   Librarian details with new password
     * @param librarianId ID of the Librarian to be updated
     * @return the message
     */
    @PutMapping(path = "{librarianId}/changepassword")
    public ResponseEntity<?> changePassword(final @RequestBody Librarian l,
            final @PathVariable("librarianId") Long librarianId) {

        LOGGER.info("changePassword: librarian = {}, librarianId = {}", l, librarianId);

        try {
            this.librarianService.changePassword(l, librarianId);

            String message = "Password updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Delete {@link Librarian} from the database and reset its associated
     * {@link Transaction}s
     *
     * @param librarianId ID of the Librarian to be deleted
     * @param deleterId   ID of the Librarian carrying out the deletion
     * @return the message
     */
    @DeleteMapping(path = "{librarianId}")
    public ResponseEntity<?> deleteLibrarian(final @PathVariable("librarianId") Long librarianId,
            final @RequestParam(name = "deleteBy", required = true) Long deleterId) {

        LOGGER.info("deleteLibrarian: librarianId = {}", librarianId);

        try {
            this.librarianService.deleteLibrarian(librarianId, deleterId);

            String message = "Librarian deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}
