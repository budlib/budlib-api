package com.budlib.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Loaner;
import com.budlib.api.model.Transaction;
import com.budlib.api.response.ErrorBody;
import com.budlib.api.service.LoanerService;

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
 * Controller for loaner
 */
@CrossOrigin
@RestController
@RequestMapping("api/loaners")
public class LoanerController {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanerController.class);

    /**
     * {@link LoanerService} for interacting with Loaners
     */
    private final LoanerService loanerService;

    /**
     * Constructor
     *
     * @param ls {@code LoanerService} for interaction with Loaners
     */
    @Autowired
    public LoanerController(final LoanerService ls) {

        LOGGER.debug("LoanerController: injected LoanerService");

        this.loanerService = ls;
    }

    /**
     * Get {@link Loaner} by ID
     *
     * @param loanerId ID of the Loaner
     * @return Loaner
     */
    @GetMapping(path = "{loanerId}")
    public ResponseEntity<?> getLoanerById(final @PathVariable("loanerId") Long loanerId) {

        LOGGER.info("getLoanerById: loanerId = {}", loanerId);

        Loaner l = this.loanerService.getLoanerById(loanerId);

        if (l == null) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(l);
        }
    }

    /**
     * Get list of {@link Transaction} history of the {@link Loaner}
     *
     * @param loanerId Loaner ID whose transaction history is required
     * @return list of Transactions of the Loaner
     */
    @GetMapping(path = "{loanerId}/history")
    public ResponseEntity<?> getTransactionHistory(final @PathVariable("loanerId") Long loanerId) {

        LOGGER.info("getTransactionHistory: loanerId = {}", loanerId);

        try {
            List<Transaction> trnList = this.loanerService.getTransactionHistory(loanerId);
            return ResponseEntity.status(HttpStatus.OK).body(trnList);
        }

        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Get list of {@link Transaction} history of the {@link Loaner}
     *
     * @param loanerId Loaner ID whose transaction history is required
     * @return list of Transactions of the Loaner
     */
    @GetMapping(path = "{loanerId}/loans")
    public ResponseEntity<?> getCurrentLoans(final @PathVariable("loanerId") Long loanerId) {

        LOGGER.info("getCurrentLoans: loanerId = {}", loanerId);

        try {
            List<Loan> loanList = this.loanerService.getCurrentLoans(loanerId);
            return ResponseEntity.status(HttpStatus.OK).body(loanList);
        }

        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Search {@link Loaner} in the database
     *
     * @param schoolId   school ID of the Loaner
     * @param name       name of the Loaner
     * @param parentName parent's name of the Loaner
     * @return list of Loaners that match the search parameters
     */
    @GetMapping()
    public ResponseEntity<?> searchLoaner(final @RequestParam(name = "schoolid", required = false) String schoolId,
            final @RequestParam(name = "name", required = false) String name,
            final @RequestParam(name = "parentname", required = false) String parentName) {

        StringBuilder sb = new StringBuilder();
        sb.append("schoolid = ").append(schoolId).append(", ");
        sb.append("name = ").append(name).append(", ");
        sb.append("parentname = ").append(parentName).append(", ");

        LOGGER.info("searchLoaner: parameters = {}", sb.toString());

        Map<String, String> parameters = new HashMap<>();

        if (schoolId != null) {
            parameters.put("schoolid", schoolId);
        }

        if (name != null) {
            parameters.put("name", name);
        }

        if (parentName != null) {
            parameters.put("parentname", parentName);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.loanerService.searchLoaner(parameters));

    }

    /**
     * Save the {@link Loaner} in the database
     *
     * @param loaner Loaner details
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addLoaner(final @RequestBody Loaner loaner) {

        LOGGER.info("addLoaner: loaner = {}", loaner);

        try {
            this.loanerService.addLoaner(loaner);

            String message = "Loaner added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Update the {@link Loaner} in the database
     *
     * @param loaner   Loaner details
     * @param loanerId ID of the Loaner to be updated
     * @return the message
     */
    @PutMapping(path = "{loanerId}")
    public ResponseEntity<?> updateLoaner(final @RequestBody Loaner l, final @PathVariable("loanerId") Long loanerId) {

        LOGGER.info("updateLoaner: loaner = {}, loanerId = {}", l, loanerId);

        try {
            this.loanerService.updateLoaner(l, loanerId);

            String message = "Loaner updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Delete {@link Loaner} from the database and reset its associated
     * {@link Transaction}s
     *
     * @param loanerId ID of the Loaner to be deleted
     * @return the message
     */
    @DeleteMapping(path = "{loanerId}")
    public ResponseEntity<?> deleteLoaner(final @PathVariable("loanerId") Long loanerId) {

        LOGGER.info("deleteLoaner: loanerId = {}", loanerId);

        try {
            this.loanerService.deleteLoaner(loanerId);

            String message = "Loaner deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Import the CSV file that was uploaded, with the help of fields mapping
     * provided
     *
     * @param loanerCsv the fields mapping
     * @return the message
     */
    @PostMapping(path = "import")
    public ResponseEntity<?> importLoaners(@RequestBody Map<String, String> loanerCsv) {

        LOGGER.info("importLoaners: loanerCsv map = {}", loanerCsv);

        try {
            int importedLoaners = this.loanerService.importLoaner(loanerCsv);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ErrorBody(HttpStatus.OK, String.format("%d loaners imported successfully", importedLoaners)));
        }

        catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}
