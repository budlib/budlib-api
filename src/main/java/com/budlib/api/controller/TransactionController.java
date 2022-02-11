package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for transactions
 */
@CrossOrigin
@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Search the transaction by id
     *
     * @param id transaction id
     * @return list of transaction with id; the list would have utmost one element
     */
    private List<Transaction> searchTransactionById(Long id) {
        Optional<Transaction> transactionOptional = this.transactionRepository.findById(id);

        if (transactionOptional.isPresent()) {
            List<Transaction> searchResults = new ArrayList<>();
            searchResults.add(transactionOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search the transaction by loaner
     *
     * @param allTransactions list of all transactions
     * @param sT              search term
     * @return filtered list of transaction with loaner meeting the search term
     */
    private List<Transaction> searchTransactionByLoaner(List<Transaction> allTransactions, String sT) {
        List<Transaction> searchResults = new ArrayList<>();

        try {
            long searchTerm = Long.parseLong(sT.toLowerCase());

            for (Transaction eachTransaction : allTransactions) {
                if (eachTransaction.getLoaner() != null
                        && eachTransaction.getLoaner().getLoanerId() == searchTerm) {
                    searchResults.add(eachTransaction);
                }
            }

            return searchResults;
        }

        catch (NumberFormatException e) {
            return searchResults;
        }
    }

    /**
     * Search the transaction by librarian
     *
     * @param allTransactions list of all transactions
     * @param sT              search term
     * @return filtered list of transaction with librarian meeting the search term
     */
    private List<Transaction> searchTransactionByLibrarian(List<Transaction> allTransactions, String sT) {
        List<Transaction> searchResults = new ArrayList<>();

        try {
            long searchTerm = Long.parseLong(sT.toLowerCase());

            for (Transaction eachTransaction : allTransactions) {
                if (eachTransaction.getLibrarian() != null
                        && eachTransaction.getLibrarian().getLibrarianId() == searchTerm) {
                    searchResults.add(eachTransaction);
                }
            }

            return searchResults;
        }

        catch (NumberFormatException e) {
            return searchResults;
        }
    }

    /**
     * Search the transactions by their transaction type
     *
     * @param allTransactions list of all transactions
     * @param sT              search term
     * @return filtered list of transactions with type meeting the search term
     */
    private List<Transaction> searchTransactionByType(List<Transaction> allTransactions, String sT) {
        List<Transaction> searchResults = new ArrayList<>();

        try {
            TransactionType searchTerm = TransactionType.valueOf(sT.toUpperCase());

            for (Transaction eachTransaction : allTransactions) {
                if (eachTransaction.getTransactionType() == searchTerm) {
                    searchResults.add(eachTransaction);
                }
            }

            return searchResults;
        }

        catch (IllegalArgumentException e) {
            return searchResults;
        }
    }

    /**
     * Endpoint for GET - fetch transaction by id
     *
     * @param id transaction id
     * @return transaction
     */
    @GetMapping(path = "{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable("transactionId") Long id) {
        List<Transaction> t = this.searchTransactionById(id);

        if (t == null) {
            String message = "Transaction not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(t.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all transactions meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of transactions meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchTransactions(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<Transaction> allTransactions = this.transactionRepository.findAll();

        if (searchBy == null || searchTerm == null) {
            return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
        }

        else if (searchBy.equals("") || searchTerm.equals("")) {
            return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
        }

        else if (searchBy.equalsIgnoreCase("id")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchTransactionById(Long.valueOf(searchTerm)));
        }

        else if (searchBy.equalsIgnoreCase("loaner")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.searchTransactionByLoaner(allTransactions, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("librarian")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.searchTransactionByLibrarian(allTransactions, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("type")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchTransactionByType(allTransactions, searchTerm));
        }

        else {
            String message = "Invalid transaction search operation";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Endpoint for POST - save the transaction in db
     *
     * @param t transaction details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Transaction t) {
        // reset the id to 0 to prevent overwrite
        t.setTransactionId(0L);

        this.transactionRepository.save(t);

        String message = "Transaction added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for PUT - update transaction in db
     *
     * @param t             the updated transaction details in json
     * @param transactionId the id of the transaction to be updated
     * @return the message
     */
    @PutMapping(path = "{transactionId}")
    public ResponseEntity<?> updateTransaction(@RequestBody Transaction t,
            @PathVariable("transactionId") Long transactionId) {
        Optional<Transaction> transactionOptional = this.transactionRepository.findById(transactionId);

        if (transactionOptional.isPresent()) {
            t.setTransactionId(transactionId);
            this.transactionRepository.save(t);

            String message = "Transaction updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        else {
            String message = "Transaction not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }
    }

    /**
     * Endpoint for DELETE - delete transaction from db
     *
     * @param transactionId transaction id
     */
    @DeleteMapping(path = "{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("transactionId") Long transactionId) {
        if (!this.transactionRepository.existsById(transactionId)) {
            String message = "Transaction not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            this.transactionRepository.deleteById(transactionId);
            String message = "Transaction deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
