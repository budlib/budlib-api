package com.budlib.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Book;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Tag;
import com.budlib.api.response.ErrorBody;
import com.budlib.api.service.BookService;

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
 * Controller for Books
 */
@CrossOrigin
@RestController
@RequestMapping("api/books")
public class BookController {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    /**
     * {@link BookService} for interacting with Books
     */
    private final BookService bookService;

    /**
     * Constructor
     *
     * @param bs {@code BookService} for interaction with Books
     */
    @Autowired
    public BookController(final BookService bs) {

        LOGGER.debug("BookController: injected BookService");

        this.bookService = bs;
    }

    /**
     * Get {@link Book} by ID
     *
     * @param bookId ID of the Book
     * @return Book
     */
    @GetMapping(path = "{bookId}")
    public ResponseEntity<?> getBookById(final @PathVariable("bookId") Long bookId) {

        LOGGER.info("getBookById: bookId = {}", bookId);

        Book b = this.bookService.getBookById(bookId);

        if (b == null) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(b);
        }
    }

    /**
     * Get list of {@link Tag} of a {@link Book} by Book ID
     *
     * @param bookId ID of the Book
     * @return list of Tags
     */
    @GetMapping(path = "{bookId}/tags")
    public ResponseEntity<?> getBookTags(final @PathVariable("bookId") Long bookId) {

        LOGGER.info("getBookTags: bookId = {}", bookId);

        try {
            List<Tag> tagList = this.bookService.getBookTags(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(tagList);
        }

        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Get list of current {@link Loan} of a {@link Book} by Book ID
     *
     * @param bookId ID of the Book
     * @return list of Loans
     */
    @GetMapping(path = "{bookId}/loans")
    public ResponseEntity<?> getCurrentLoans(final @PathVariable("bookId") Long bookId) {

        LOGGER.info("getCurrentLoans: bookId = {}", bookId);

        try {
            List<Loan> loanList = this.bookService.getCurrentLoans(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(loanList);
        }

        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Search {@link Book} in the database
     *
     * @param title          title of Book to filter
     * @param subtitle       subtitle of Book to filter
     * @param author         author of Book to filter
     * @param publisher      publisher of Book to filter
     * @param isbn           ISBN of Book to filter
     * @param librarySection LibrarySection of Book to filter
     * @param tags           Tag of Book to filter
     * @param language       language of Book to filter
     * @return list of Books that match the filters
     */
    @GetMapping()
    public ResponseEntity<?> searchBook(final @RequestParam(name = "title", required = false) String title,
            final @RequestParam(name = "subtitle", required = false) String subtitle,
            final @RequestParam(name = "author", required = false) String author,
            final @RequestParam(name = "publisher", required = false) String publisher,
            final @RequestParam(name = "isbn", required = false) String isbn,
            final @RequestParam(name = "librarysection", required = false) String librarySection,
            final @RequestParam(name = "tags", required = false) String tags,
            final @RequestParam(name = "language", required = false) String language) {

        StringBuilder sb = new StringBuilder();
        sb.append("title = ").append(title).append(", ");
        sb.append("subtitle = ").append(subtitle).append(", ");
        sb.append("author = ").append(author).append(", ");
        sb.append("publisher = ").append(publisher).append(", ");
        sb.append("isbn = ").append(isbn).append(", ");
        sb.append("librarySection = ").append(librarySection).append(", ");
        sb.append("tags = ").append(tags).append(", ");
        sb.append("language = ").append(language);

        LOGGER.info("searchBook: parameters = {}", sb.toString());

        Map<String, String> parameters = new HashMap<>();

        if (title != null) {
            parameters.put("title", title);
        }

        if (subtitle != null) {
            parameters.put("subtitle", subtitle);
        }

        if (author != null) {
            parameters.put("author", author);
        }

        if (publisher != null) {
            parameters.put("publisher", publisher);
        }

        if (isbn != null) {
            parameters.put("isbn", isbn);
        }

        if (librarySection != null) {
            parameters.put("librarysection", librarySection);
        }

        if (tags != null) {
            parameters.put("tags", tags);
        }

        if (language != null) {
            parameters.put("language", language);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.bookService.searchBook(parameters));
    }

    /**
     * Save the {@link Book} in the database
     *
     * @param book Book details
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addBook(final @RequestBody Book book) {

        LOGGER.info("addBook: book = {}", book);

        try {
            this.bookService.addBook(book);

            String message = "Book added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Update {@link Book} in the database
     *
     * @param book   updated Book details
     * @param bookId ID of the Book to be updated
     * @return the message
     */
    @PutMapping(path = "{bookId}")
    public ResponseEntity<?> updateBook(final @RequestBody Book b, final @PathVariable("bookId") Long bookId) {

        LOGGER.info("updateBook: book = {}, bookId = {}", b, bookId);

        try {
            this.bookService.updateBook(b, bookId);

            String message = "Book updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        catch (UserInputException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Delete {@link Book} from the database
     *
     * @param bookId ID of the Book to be deleted
     * @return the message
     */
    @DeleteMapping(path = "{bookId}")
    public ResponseEntity<?> deleteBook(final @PathVariable("bookId") Long bookId) {

        LOGGER.info("deleteBook: bookId = {}", bookId);

        try {
            this.bookService.deleteBook(bookId);

            String message = "Book deleted successfully";
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
     * @param bookCsv the fields mapping
     * @return the message
     */
    @PostMapping(path = "import")
    public ResponseEntity<?> importBooks(final @RequestBody Map<String, String> bookCsv) {

        LOGGER.info("importBooks: bookCsv map = {}", bookCsv);

        try {
            int importedBooks = this.bookService.importBooks(bookCsv);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorBody(HttpStatus.OK, String.format("%d books imported successfully", importedBooks)));
        }

        catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}
