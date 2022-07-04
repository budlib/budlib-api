package com.budlib.api.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.budlib.api.model.Book;
import com.budlib.api.model.Librarian;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Loaner;
import com.budlib.api.model.Tag;
import com.budlib.api.model.Transaction;
import com.budlib.api.model.TrnQuantities;
import com.budlib.api.repository.BookRepository;
import com.budlib.api.repository.LoanRepository;
import com.budlib.api.repository.LoanerRepository;
import com.budlib.api.repository.TransactionRepository;
import com.budlib.api.response.ErrorBody;
import com.budlib.api.response.Stats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for dashboard
 */
@CrossOrigin
@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanerRepository loanerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DashboardController(
            final BookRepository bookR,
            final LoanRepository loanR,
            final LoanerRepository loanerR,
            final TransactionRepository transactionR) {

        this.bookRepository = bookR;
        this.loanRepository = loanR;
        this.loanerRepository = loanerR;
        this.transactionRepository = transactionR;
    }

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
            countTotalCopies = countTotalCopies + eachBook.getTotalQuantity();
        }

        currentStats.setTotalCopies(countTotalCopies);

        long countTotalOutstandingCopies = 0;
        List<Loan> allLoans = this.loanRepository.findAll();

        for (Loan eachLoan : allLoans) {
            countTotalOutstandingCopies = countTotalOutstandingCopies + eachLoan.getCopies();
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
     * Get the list of loans that are due today or in next week
     *
     * @return list of upcoming loans today or in the next week
     */
    @GetMapping(path = "upcomingdue")
    public ResponseEntity<?> getUpcomingDueList() {
        List<Loan> allLoans = this.loanRepository.findAll();

        List<Loan> upcomingDueLoans = new ArrayList<>();
        LocalDate dateNow = LocalDate.now();

        for (Loan eachLoan : allLoans) {
            long deltaDays = ChronoUnit.DAYS.between(dateNow, eachLoan.getDueDate());

            if (deltaDays >= 0 && deltaDays <= 7) {
                upcomingDueLoans.add(eachLoan);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(upcomingDueLoans);
    }

    /**
     * Downloads the give filename via API call
     *
     * @param fileName the file on the filesystem to download
     * @return the file to download via API call
     */
    private ResponseEntity<?> fileDownloader(String fileName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

            File outFile = new File(fileName);
            Path path = Paths.get(outFile.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(outFile.length())
                    .body(resource);
        }

        catch (IOException e) {
            String message = "Error while downloading file";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Export all the books in the database into CSV
     *
     * @return CSV containing snapshot of all the books in the database
     */
    @GetMapping(path = "batch/export/books")
    public ResponseEntity<?> exportBooks() {
        String[] bookHeaders = { "bookId", "title", "subtitle", "authors", "publisher", "edition", "year", "language",
                "isbn10", "isbn13", "librarySection", "totalQuantity", "availableQuantity", "notes", "imageLink",
                "retailPrice", "libraryPrice", "tags" };

        List<Book> allBooks = this.bookRepository.findAll();

        try {
            Path tempPath = Files.createTempFile("budlib_books_export", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
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
                record[10] = eachBook.getLibrarySection().toString();
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

            return this.fileDownloader(tempFile.getAbsolutePath());
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
            Path tempPath = Files.createTempFile("budlib_loaners_export", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
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

            return this.fileDownloader(tempFile.getAbsolutePath());
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
            Path tempPath = Files.createTempFile("budlib_outstanding_loans_export", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
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

            return this.fileDownloader(tempFile.getAbsolutePath());
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
            Path tempPath = Files.createTempFile("budlib_transactions_export", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(trnHeaders));

            for (Transaction eachTrn : allTrns) {
                String[] record = new String[trnHeaders.length];

                Librarian correspondingFacilitator = eachTrn.getLibrarian();
                Loaner correspondingLoaner = eachTrn.getLoaner();
                List<TrnQuantities> correspondingAllTrnQty = eachTrn.getBookCopies();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

                record[0] = String.valueOf(eachTrn.getTransactionId());
                record[1] = eachTrn.getTransactionDateTime().format(formatter);
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

            return this.fileDownloader(tempFile.getAbsolutePath());
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Download the sample file to import the books in the database
     *
     * @return file containing sample books in CSV format
     */
    @GetMapping(path = "batch/samplebooks")
    public ResponseEntity<?> showImportSampleBooks() {
        String[] bookHeaders = { "title", "subtitle", "authors", "publisher", "edition", "year", "language", "isbn10",
                "isbn13", "librarySection", "totalQuantity", "availableQuantity", "notes", "imageLink", "retailPrice",
        "libraryPrice" };

        try {
            Path tempPath = Files.createTempFile("sample_books_import", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(bookHeaders));

            printer.printRecord(
                    "How to Stop Losing Your Sh*t with Your Kids",
                    "A Practical Guide to Becoming a Calmer, Happier Parent",
                    "Carla Naumburg",
                    "Workman Publishing Company",
                    "",
                    "2019-08-20",
                    "en",
                    "1523505427",
                    "9781523505425",
                    "PARENT_LIBRARY",
                    5,
                    5,
                    "",
                    "https://images-na.ssl-images-amazon.com/images/I/51TFx51B-FL._SX357_BO1,204,203,200_.jpg",
                    21.73,
                    19);

            printer.printRecord(
                    "I Spy - Everything!",
                    "A Fun Guessing Game for 2-4 Year Olds",
                    "Books For Little Ones",
                    "",
                    "",
                    "2018-03-19",
                    "en",
                    "1980596743",
                    "9781980596745",
                    "CHILDREN_LIBRARY",
                    7,
                    7,
                    "",
                    "https://m.media-amazon.com/images/P/1980596743.01._SCLZZZZZZZ_SX500_.jpg",
                    10.8,
                    5.99);

            printer.printRecord(
                    "Student Engagement Techniques",
                    "A Handbook for College Faculty",
                    "Elizabeth F. Barkley,Claire H. Major",
                    "John Wiley & Sons",
                    "",
                    "2020-05-05",
                    "en",
                    "1119686776",
                    "9781119686774",
                    "FACULTY_LIBRARY",
                    2,
                    2,
                    "",
                    "https://images-na.ssl-images-amazon.com/images/I/81G8F2OV9SL.jpg",
                    53.45,
                    50.5);

            printer.printRecord(
                    "Classes Are Canceled!",
                    "",
                    "Jack Chabert",
                    "Eerie Elementary",
                    "",
                    "2017",
                    "en",
                    "1338181807",
                    "9781338181807",
                    "CHILDREN_LIBRARY",
                    15,
                    15,
                    "",
                    "https://images-na.ssl-images-amazon.com/images/I/81vHq-mWzWL.jpg",
                    7.91,
                    2.99);

            printer.close();
            pw.close();

            return this.fileDownloader(tempFile.getAbsolutePath());
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }

    /**
     * Download the sample file to import the loaners in the database
     *
     * @return file containing sample loaners in CSV format
     */
    @GetMapping(path = "batch/sampleloaners")
    public ResponseEntity<?> showImportSampleLoaners() {
        String[] loanerHeaders = { "schoolId", "isStudent", "email", "salutation", "first_name", "middle_name",
                "last_name", "mother_name", "father_name" };

        try {
            Path tempPath = Files.createTempFile("sample_loaners_import", ".csv");
            File tempFile = tempPath.toFile();

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, false)), true);
            CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT.withHeader(loanerHeaders));

            printer.printRecord("STU-1001", true, "", "Miss", "Josephine", "", "Murray", "Yazmin Murray", "Adonis Murray");
            printer.printRecord("STU-1002", true, "", "Mr", "Mildred", "", "Gonzales", "Micaela Gonzales", "Rhett Gonzales");
            printer.printRecord("STU-1003", true, "", "Miss", "Loretta", "", "Carrillo", "Esperanza Carrillo", "Roland Carrillo");
            printer.printRecord("FAC-1001", false, "", "Mr", "Dwight", "", "Schrute", "", "");
            printer.printRecord("FAC-1002", false, "", "Miss", "Angela", "", "Martin", "", "");

            printer.close();
            pw.close();

            return this.fileDownloader(tempFile.getAbsolutePath());
        }

        catch (IOException e) {
            String message = "Error while processing export request";
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ErrorBody(HttpStatus.GATEWAY_TIMEOUT, message));
        }
    }
}
