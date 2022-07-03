package com.budlib.api.controller;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.budlib.api.enums.TransactionType;
import com.budlib.api.model.Book;
import com.budlib.api.model.Librarian;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Loaner;
import com.budlib.api.model.Transaction;
import com.budlib.api.model.TrnQuantities;
import com.budlib.api.repository.BookRepository;
import com.budlib.api.repository.LibrarianRepository;
import com.budlib.api.repository.LoanRepository;
import com.budlib.api.repository.LoanerRepository;
import com.budlib.api.repository.TransactionRepository;
import com.budlib.api.repository.TrnQuantitiesRepository;
import com.budlib.api.response.ErrorBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for transactions
 */
@CrossOrigin
@RestController
@Transactional
@RequestMapping("api/transactions")
public class TransactionController {

    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final TrnQuantitiesRepository trnQuantitiesRepository;
    private final LibrarianRepository librarianRepository;
    private final LoanerRepository loanerRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public TransactionController(
            final BookRepository bookR,
            final TransactionRepository transactionR,
            final TrnQuantitiesRepository trnQuantitiesR,
            final LibrarianRepository librarianR,
            final LoanerRepository loanerR,
            final LoanRepository loanR) {

        this.bookRepository = bookR;
        this.transactionRepository = transactionR;
        this.trnQuantitiesRepository = trnQuantitiesR;
        this.librarianRepository = librarianR;
        this.loanerRepository = loanerR;
        this.loanRepository = loanR;
    }

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
                if (eachTransaction.getLoaner() != null && eachTransaction.getLoaner().getLoanerId() == searchTerm) {
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

        try {
            if (searchBy == null || searchTerm == null) {
                return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
            }

            else if (searchBy.equals("") || searchTerm.equals("")) {
                return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
            }

            else if (searchBy.equalsIgnoreCase("id")) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.searchTransactionById(Long.valueOf(searchTerm)));
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
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.searchTransactionByType(allTransactions, searchTerm));
            }

            else {
                // String message = "Invalid transaction search operation";
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                // .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
            }
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(allTransactions);
        }
    }

    /**
     * Check if the loaner exists in the system. Works only if Loaner.schoolId is
     * not null or empty. This method exists in LoanerController as well.
     *
     * @param l Loaner to be added or updated
     * @return true if unique, false otherwise
     */
    @SuppressWarnings("unused")
    private boolean checkLoanerUniqueness(Loaner l) {
        if (l.getSchoolId() == null || l.getSchoolId().equals("")) {
            return true;
        }

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getLoanerId() == l.getLoanerId()) {
                continue;
            }

            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().equalsIgnoreCase(l.getSchoolId())) {
                return false;
            }
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
    public ResponseEntity<?> addTransaction(
            @RequestBody Transaction t,
            @RequestParam(name = "borrowDate", required = false) String suppliedBorrowDateString,
            @RequestParam(name = "dueDate", required = false) String suppliedDueDateString) {

        // reset the id to 0 to prevent overwrite
        t.setTransactionId(0L);

        // reset the transaction date time
        t.setTransactionDateTime(ZonedDateTime.now());

        Loaner suppliedLoaner = t.getLoaner();
        Librarian suppliedLibrarian = t.getLibrarian();
        List<TrnQuantities> suppliedTrnQtyList = t.getBookCopies();

        List<TrnQuantities> checkedTrnQtyList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // initializing with default values
        LocalDate suppliedBorrowDate = t.getTransactionDateTime().toLocalDate();
        LocalDate suppliedDueDate = t.getTransactionDateTime().toLocalDate().plusWeeks(4);

        // check validity of dates
        // --------------------------------------------------------------------
        if (t.getTransactionType().equals(TransactionType.BORROW)) {
            // borrow date must be specified when borrowing
            if (suppliedBorrowDateString == null || suppliedBorrowDateString.equals("")) {
                String message = "Borrow date not specified";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            try {
                suppliedBorrowDate = LocalDate.parse(suppliedBorrowDateString, formatter);
            }

            catch (DateTimeParseException e) {
                String message = "Invalid borrow date specified";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            catch (Exception e) {
                String message = e.getMessage();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            // if due date is not specified, default is 4 weeks
            if (suppliedDueDateString == null || suppliedDueDateString.equals("")) {
                suppliedDueDate = suppliedBorrowDate.plusWeeks(4);
            }

            // else try to parse what user has supplied
            else {
                try {
                    suppliedDueDate = LocalDate.parse(suppliedDueDateString, formatter);

                    if (suppliedDueDate.compareTo(suppliedBorrowDate) < 0) {
                        throw new Exception("Borrow date cannot be after due date");
                    }
                }

                catch (DateTimeParseException e) {
                    String message = "Invalid due date specified";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                }

                catch (Exception e) {
                    String message = e.getMessage();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                }
            }
        }

        // due date must be specified when providing extension
        else if (t.getTransactionType().equals(TransactionType.EXTEND)) {
            if (suppliedDueDateString == null || suppliedDueDateString.equals("")) {
                String message = "Due date not specified";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            try {
                suppliedDueDate = LocalDate.parse(suppliedDueDateString, formatter);
            }

            catch (DateTimeParseException e) {
                String message = "Invalid due date specified";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            catch (Exception e) {
                String message = e.getMessage();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }
        }
        // --------------------------------------------------------------------

        // check validity of librarian
        // --------------------------------------------------------------------
        if (suppliedLibrarian == null) {
            String message = "Librarian coordinator not specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
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

        // check validity of loaner
        // --------------------------------------------------------------------
        if (suppliedLoaner == null) {
            String message = "No loaner specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
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
        // --------------------------------------------------------------------

        // check validity of books
        // --------------------------------------------------------------------
        if (suppliedTrnQtyList == null || suppliedTrnQtyList.isEmpty()) {
            String message = "No books specified";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            // BUGFIX: aggregate the quantities of the books - if the same book is specified
            // more than once (different loanIds), the quantity is added up
            List<TrnQuantities> aggregatedTrnQtyList = new ArrayList<>();

            for (TrnQuantities stq : suppliedTrnQtyList) {
                boolean found = false;

                for (TrnQuantities atq : aggregatedTrnQtyList) {
                    if (stq.getBook().getBookId() == atq.getBook().getBookId()) {
                        int oldCopies = atq.getCopies();

                        atq.setCopies(oldCopies + stq.getCopies());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    aggregatedTrnQtyList.add(stq);
                }
            }

            for (TrnQuantities tq : aggregatedTrnQtyList) {
                if (tq.getCopies() < 1) {
                    String message = "Invalid quantity specified";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                }

                Optional<Book> bookOptional = this.bookRepository.findById(tq.getBook().getBookId());

                if (bookOptional.isPresent()) {
                    Book b = bookOptional.get();

                    // cannot borrow if book has no copies left
                    if (t.getTransactionType().equals(TransactionType.BORROW)
                            && tq.getCopies() > b.getAvailableQuantity()) {
                        String message = "Not enough copies available";
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                    }

                    // cannot return more than you borrowed
                    else if (t.getTransactionType().equals(TransactionType.RETURN)
                            && tq.getCopies() > suppliedLoaner.findOutstandingCopiesByBook(b)) {
                        String message = "Cannot return more than what was borrowed";
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                    }

                    // cannot extend partial number of copies of the same book
                    else if (t.getTransactionType().equals(TransactionType.EXTEND)
                            && tq.getCopies() != suppliedLoaner.findOutstandingCopiesByBook(b)) {
                        String message = "Cannot extend partial number of copies";
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

        // save transaction
        Transaction savedTrn = this.transactionRepository.save(t);

        // current loans
        List<Loan> currentLoans = suppliedLoaner.getCurrentLoans();

        // save book quantities and transaction quantities
        for (TrnQuantities ctq : checkedTrnQtyList) {
            ctq.setTransaction(savedTrn);

            Book ctqb = ctq.getBook();
            int initialQty = ctqb.getAvailableQuantity();

            if (savedTrn.getTransactionType().equals(TransactionType.BORROW)) {
                ctqb.setAvailableQuantity(initialQty - ctq.getCopies());

                Loan newLoan = new Loan(0L, suppliedLoaner, ctqb, ctq.getCopies(), suppliedBorrowDate, suppliedDueDate);
                this.loanRepository.save(newLoan);

                currentLoans.add(newLoan);
                // suppliedLoaner.setCurrentLoans(currentLoans); // redundant statement
                this.loanerRepository.save(suppliedLoaner);
            }

            else if (savedTrn.getTransactionType().equals(TransactionType.RETURN)) {
                ctqb.setAvailableQuantity(initialQty + ctq.getCopies());

                // this list will have only atmost one element
                List<Loan> loansToBeDeleted = suppliedLoaner.updateLoans(ctqb, ctq.getCopies());

                for (Loan deleteLoan : loansToBeDeleted) {
                    this.loanRepository.delete(deleteLoan);
                }

                this.loanerRepository.save(suppliedLoaner);
            }

            else {
                suppliedLoaner.updateLoanDueDate(ctqb, suppliedDueDate);
                this.loanerRepository.save(suppliedLoaner);
            }

            this.bookRepository.save(ctqb);
            this.trnQuantitiesRepository.save(ctq);
        }

        String message = "Transaction completed successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }
}
