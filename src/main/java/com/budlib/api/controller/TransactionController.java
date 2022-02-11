package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;
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

    @Autowired
    private LoanerRepository loanerRepository;

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TrnQuantitiesRepository trnQuantitiesRepository;

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
     * Check if the loaner exists in the system. Works only if Loaner.schoolId is
     * not null or empty. This method exists in LoanerController as well.
     *
     * @param l Loaner to be added or updated
     * @return true if unique, false otherwise
     */
    private boolean checkLoanerUniqueness(Loaner l) {
        if (l.getSchoolId() == null || l.getSchoolId().equals("")) {
            return true;
        }

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getLoanerId() == l.getLoanerId()) {
                continue;
            }

            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().equalsIgnoreCase(l.getSchoolId()))
                return false;
        }

        return true;
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

        // reset the transaction date time
        t.setTransactionDateTime(LocalDateTime.now());

        Loaner suppliedLoaner = t.getLoaner();
        Librarian suppliedLibrarian = t.getLibrarian();
        List<TrnQuantities> suppliedTrnQtyList = t.getBookCopies();

        List<TrnQuantities> checkedTrnQtyList = new ArrayList<>();

        // check validity of librarian
        // --------------------------------------------------------------------
        if (suppliedLibrarian == null) {
            String message = "No librarian coordinator specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            Optional<Librarian> librarianOptional = this.librarianRepository
                    .findById(suppliedLibrarian.getLibrarianId());

            if (librarianOptional.isPresent()) {
                // overwrite supplied details of the librarian
                suppliedLibrarian = librarianOptional.get();
                t.setLibrarian(suppliedLibrarian);
            }

            else {
                String message = "Librarian not found";
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorBody(HttpStatus.NOT_FOUND, message));
            }
        }
        // --------------------------------------------------------------------

        // check validity of books
        // --------------------------------------------------------------------
        if (suppliedTrnQtyList == null) {
            String message = "No books specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            for (TrnQuantities tq : suppliedTrnQtyList) {
                if (tq.getCopies() < 1) {
                    String message = "Invalid quantity specified";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                }

                Optional<Book> bookOptional = this.bookRepository.findById(tq.getBook().getBookId());

                if (bookOptional.isPresent()) {
                    Book b = bookOptional.get();

                    if (t.getTransactionType().equals(TransactionType.BORROW)
                            && tq.getCopies() > b.getAvailableQuantity()) {
                        String message = "Not enough copies available";
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                    }

                    else {
                        tq.setBook(b);
                        checkedTrnQtyList.add(tq);
                    }
                }

                else {
                    String message = "One or more books not found";
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorBody(HttpStatus.NOT_FOUND, message));
                }
            }

            t.setBookCopies(checkedTrnQtyList);
        }
        // --------------------------------------------------------------------

        // check validity of loaner
        // --------------------------------------------------------------------
        if (suppliedLoaner == null) {
            String message = "No loaner specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            // for loaners not existing in system - save them before doing the transaction
            if (suppliedLoaner.getLoanerId() == 0L) {
                if (this.checkLoanerUniqueness(suppliedLoaner)) {
                    this.loanerRepository.save(suppliedLoaner);
                }

                else {
                    String message = "Loaner already exists";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                }
            }

            else {
                // for loaners existing in system - find them before doing the transaction
                Optional<Loaner> loanerOptional = this.loanerRepository.findById(suppliedLoaner.getLoanerId());

                if (loanerOptional.isPresent()) {
                    // overwrite supplied details of the loaner
                    suppliedLoaner = loanerOptional.get();
                    t.setLoaner(suppliedLoaner);
                }

                else {
                    String message = "Loaner not found";
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorBody(HttpStatus.NOT_FOUND, message));
                }
            }
        }
        // --------------------------------------------------------------------

        // save transaction
        Transaction savedTrn = this.transactionRepository.save(t);

        // save book quantities and transaction quantities
        for (TrnQuantities ctq : checkedTrnQtyList) {
            ctq.setTransaction(savedTrn);

            Book ctqb = ctq.getBook();
            int initialQty = ctqb.getAvailableQuantity();

            if (t.getTransactionType().equals(TransactionType.BORROW)) {
                ctqb.setAvailableQuantity(initialQty - ctq.getCopies());
            }

            else if (t.getTransactionType().equals(TransactionType.RETURN)) {
                ctqb.setAvailableQuantity(initialQty + ctq.getCopies());
            }

            this.bookRepository.save(ctqb);
            this.trnQuantitiesRepository.save(ctq);
        }

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
