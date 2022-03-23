package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import com.budlib.api.response.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;

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

    @Autowired
    private TransactionRepository transactionRepository;

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

    /**
     * Export all the books in the database into CSV
     *
     * @return CSV containing snapshot of all the books in the database
     */
    @GetMapping(path = "batch/export/books")
    public ResponseEntity<?> exportBooks() {
        String[] bookHeaders = { "bookId", "title", "subtitle", "authors", "publisher", "edition",
                "year", "language", "isbn10", "isbn13", "librarySection", "totalQuantity", "availableQuantity", "notes",
                "imageLink", "retailPrice", "libraryPrice", "tags" };

        List<Book> allBooks = this.bookRepository.findAll();

        try {
            new File("exports").mkdirs();
            String exportFileName = "exports/budlib_books_export.csv";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportFileName + "\"");

            File outFile = new File(exportFileName);

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(bookHeaders));

            for (Book eachBook : allBooks) {
                String[] record = new String[bookHeaders.length];

                record[0] = String.valueOf(eachBook.getBookId());
                record[1] = eachBook.getTitle();
                record[2] = eachBook.getSubtitle();
                record[3] = eachBook.getAuthors();
                record[4] = eachBook.getPublisher();
                record[5] = eachBook.getEdition();
                record[6] = eachBook.getYear();
                record[7] = eachBook.getLanguage();
                record[8] = eachBook.getIsbn_10();
                record[9] = eachBook.getIsbn_13();
                record[10] = eachBook.getLibrarySection();
                record[11] = String.valueOf(eachBook.getTotalQuantity());
                record[12] = String.valueOf(eachBook.getAvailableQuantity());
                record[13] = eachBook.getNotes();
                record[14] = eachBook.getImageLink();
                record[15] = String.valueOf(eachBook.getPriceRetail());
                record[16] = String.valueOf(eachBook.getPriceLibrary());

                List<Tag> tagList = eachBook.getTags();
                StringBuilder sb = new StringBuilder();

                for (Tag eachTag : tagList) {
                    sb.append(String.format("%s, ", eachTag.getTagName()));
                }

                record[17] = sb.toString();

                printer.printRecord(record[0], record[1], record[2], record[3], record[4], record[5], record[6],
                        record[7], record[8], record[9], record[10], record[11], record[12], record[13], record[14],
                        record[15], record[16], record[17]);
            }

            printer.close();
            pw.close();

            Path path = Paths.get(outFile.getAbsolutePath());
            System.out.println(path.toAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(outFile.length())
                    .body(resource);
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Export all the loaners in the database into CSV
     *
     * @return CSV containing snapshot of all the loaners in the database
     */
    @GetMapping(path = "batch/export/loaners")
    public ResponseEntity<?> exportLoaners() {
        String[] loanerHeaders = { "loanerId", "schoolId", "isStudent", "email", "salutation", "first_name",
                "middle_name", "last_name", "mother_name", "father_name" };

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        try {
            new File("exports").mkdirs();
            String exportFileName = "exports/budlib_loaners_export.csv";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportFileName + "\"");

            File outFile = new File(exportFileName);

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(loanerHeaders));

            for (Loaner eachLoaner : allLoaners) {
                String[] record = new String[loanerHeaders.length];

                record[0] = String.valueOf(eachLoaner.getLoanerId());
                record[1] = eachLoaner.getSchoolId();
                record[2] = String.valueOf(eachLoaner.isStudent());
                record[3] = eachLoaner.getEmail();
                record[4] = eachLoaner.getSalutation();
                record[5] = eachLoaner.getFirstName();
                record[6] = eachLoaner.getMiddleName();
                record[7] = eachLoaner.getLastName();
                record[8] = eachLoaner.getMotherName();
                record[9] = eachLoaner.getFatherName();

                printer.printRecord(record[0], record[1], record[2], record[3], record[4], record[5], record[6],
                        record[7], record[8], record[9]);
            }

            printer.close();
            pw.close();

            Path path = Paths.get(outFile.getAbsolutePath());
            System.out.println(path.toAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(outFile.length())
                    .body(resource);
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Export all the outstanding loans in the database into CSV
     *
     * @return CSV containing snapshot of all the outstanding loans in the database
     */
    @GetMapping(path = "batch/export/loans")
    public ResponseEntity<?> exportOutstandingLoans() {
        String[] loanHeaders = { "loanId", "loanerId", "loanerSchoolId", "loanerEmail", "loanerFullName", "bookId",
                "bookIsbn10", "bookIsbn13", "bookTitle", "loanedCopies", "borrowDate", "dueDate" };

        List<Loan> allLoans = this.loanRepository.findAll();

        try {
            new File("exports").mkdirs();
            String exportFileName = "exports/budlib_outstanding_loans_export.csv";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportFileName + "\"");

            File outFile = new File(exportFileName);

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(loanHeaders));

            for (Loan eachLoan : allLoans) {
                String[] record = new String[loanHeaders.length];

                Loaner correspondingLoaner = eachLoan.getLoaner();
                Book correspondingBook = eachLoan.getBook();

                record[0] = String.valueOf(eachLoan.getLoanId());
                record[1] = String.valueOf(correspondingLoaner.getLoanerId());
                record[2] = correspondingLoaner.getSchoolId();
                record[3] = correspondingLoaner.getEmail();
                record[4] = correspondingLoaner.getFullNameWithSalutation();
                record[5] = String.valueOf(correspondingBook.getBookId());
                record[6] = correspondingBook.getIsbn_10();
                record[7] = correspondingBook.getIsbn_13();
                record[8] = correspondingBook.getTitle();
                record[9] = String.valueOf(eachLoan.getCopies());
                record[10] = eachLoan.getBorrowDate().toString();
                record[11] = eachLoan.getDueDate().toString();

                printer.printRecord(record[0], record[1], record[2], record[3], record[4], record[5], record[6],
                        record[7], record[8], record[9], record[10], record[11]);
            }

            printer.close();
            pw.close();

            Path path = Paths.get(outFile.getAbsolutePath());
            System.out.println(path.toAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(outFile.length())
                    .body(resource);
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Export all the transactions in the database into CSV
     *
     * @return CSV containing snapshot of all the transactions in the database
     */
    @GetMapping(path = "batch/export/transactions")
    public ResponseEntity<?> exportTransactions() {
        String[] trnHeaders = { "trnId", "trnDateTime", "trnType", "facilitatorId", "facilitatorFullName",
                "facilitatorEmail", "loanerId", "loanerSchoolId", "loanerFullName", "loanerEmail", "bookId",
                "bookIsbn10", "bookIsbn13", "bookTitle", "loanedCopies" };

        List<Transaction> allTrns = this.transactionRepository.findAll();

        try {
            new File("exports").mkdirs();
            String exportFileName = "exports/budlib_transactions_export.csv";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportFileName + "\"");

            File outFile = new File(exportFileName);

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(trnHeaders));

            for (Transaction eachTrn : allTrns) {
                String[] record = new String[trnHeaders.length];

                Librarian correspondingFacilitator = eachTrn.getLibrarian();
                Loaner correspondingLoaner = eachTrn.getLoaner();
                List<TrnQuantities> correspondingAllTrnQty = eachTrn.getBookCopies();

                record[0] = String.valueOf(eachTrn.getTransactionId());
                record[1] = eachTrn.getTransactionDateTime().toString();
                record[2] = eachTrn.getTransactionType().toString();

                if (correspondingFacilitator == null) {
                    record[3] = "<removed>";
                    record[4] = "<removed>";
                    record[5] = "<removed>";
                }

                else {
                    record[3] = String.valueOf(correspondingFacilitator.getLibrarianId());
                    record[4] = correspondingFacilitator.getFullName();
                    record[5] = correspondingFacilitator.getEmail();
                }

                if (correspondingLoaner == null) {
                    record[6] = "<removed>";
                    record[7] = "<removed>";
                    record[8] = "<removed>";
                    record[9] = "<removed>";
                }

                else {
                    record[6] = String.valueOf(correspondingLoaner.getLoanerId());
                    record[7] = correspondingLoaner.getSchoolId();
                    record[8] = correspondingLoaner.getFullNameWithSalutation();
                    record[9] = correspondingLoaner.getEmail();
                }

                for (TrnQuantities eachTrnQty : correspondingAllTrnQty) {
                    Book correspondingBook = eachTrnQty.getBook();

                    record[10] = String.valueOf(correspondingBook.getBookId());
                    record[11] = correspondingBook.getIsbn_10();
                    record[12] = correspondingBook.getIsbn_13();
                    record[13] = correspondingBook.getTitle();
                    record[14] = String.valueOf(eachTrnQty.getCopies());

                    printer.printRecord(record[0], record[1], record[2], record[3], record[4], record[5], record[6],
                            record[7], record[8], record[9], record[10], record[11], record[12], record[13],
                            record[14]);
                }
            }

            printer.close();
            pw.close();

            Path path = Paths.get(outFile.getAbsolutePath());
            System.out.println(path.toAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(outFile.length())
                    .body(resource);
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }
}
