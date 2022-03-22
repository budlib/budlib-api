package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for dashboard
 */
@CrossOrigin
@RestController
@RequestMapping("api/dashboard")
public class DashboardController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanerRepository loanerRepository;

    /**
     * Computes and returns basic statistics on the count of books, loans, and
     * loaners
     *
     * @return basic statistics
     */
    @GetMapping(path = "stats")
    public ResponseEntity<?> getStats() {
        Stats currentStats = new Stats();

        currentStats.setUniqueTitles(this.bookRepository.count());
        currentStats.setTotalLoaners(this.loanerRepository.count());

        long countTotalCopies = 0;
        List<Book> allBooks = this.bookRepository.findAll();

        for (Book eachBook : allBooks) {
            countTotalCopies = countTotalCopies + (long) eachBook.getTotalQuantity();
        }

        currentStats.setTotalCopies(countTotalCopies);

        long countTotalOutstandingCopies = 0;
        List<Loan> allLoans = this.loanRepository.findAll();

        for (Loan eachLoan : allLoans) {
            countTotalOutstandingCopies = countTotalOutstandingCopies + (long) eachLoan.getCopies();
        }

        currentStats.setTotalOutstandingCopies(countTotalOutstandingCopies);

        return ResponseEntity.status(HttpStatus.OK).body(currentStats);
    }

    /**
     * Get the list of overdue loans
     *
     * @return list of loans that are overdue
     */
    @GetMapping(path = "overdue")
    public ResponseEntity<?> getOverdueList() {
        List<Loan> allLoans = this.loanRepository.findAll();

        List<Loan> overdueLoans = new ArrayList<>();
        LocalDate dateNow = LocalDate.now();

        for (Loan eachLoan : allLoans) {
            if (eachLoan.getDueDate().isBefore(dateNow)) {
                overdueLoans.add(eachLoan);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(overdueLoans);
    }

    /**
     * Get the list of loans that are due in upcoming week
     *
     * @return list of upcoming loans in the next week
     */
    @GetMapping(path = "upcomingdue")
    public ResponseEntity<?> getUpcomingDueList() {
        List<Loan> allLoans = this.loanRepository.findAll();

        List<Loan> upcomingDueLoans = new ArrayList<>();
        LocalDate dateNow = LocalDate.now();

        for (Loan eachLoan : allLoans) {
            long deltaDays = ChronoUnit.DAYS.between(dateNow, eachLoan.getDueDate());

            if (deltaDays > 0 && deltaDays <= 7) {
                upcomingDueLoans.add(eachLoan);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(upcomingDueLoans);
    }
}
