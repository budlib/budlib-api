package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/book")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * Search the books by id
     *
     * @param id book id
     * @return list of books with id; the list would have utmost one element
     */
    private List<Book> searchBookById(Long id) {
        Optional<Book> bookOptional = this.bookRepository.findById(id);

        if (bookOptional.isPresent()) {
            List<Book> searchResults = new ArrayList<>();
            searchResults.add(bookOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search the books by their title or subtitle
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with title or subtitle meeting the search term
     */
    private List<Book> searchBookByTitleOrSubtitle(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getTitle() != null && eachBook.getTitle().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }

            else if (eachBook.getSubtitle() != null && eachBook.getSubtitle().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }
        }

        return searchResults;
    }

    /**
     * Search the books by their authors
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with authors meeting the search term
     */
    private List<Book> searchBookByAuthor(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            Iterator<String> authorIterator = eachBook.getAuthors().iterator();

            while (authorIterator.hasNext()) {
                String checkAuthor = authorIterator.next();

                if (checkAuthor != null && checkAuthor.toLowerCase().contains(searchTerm)) {
                    searchResults.add(eachBook);

                    // break from while loop as we dont want to check all author if one already
                    // matched
                    break;
                }
            }
        }

        return searchResults;
    }

    /**
     * Search the books by their publisher
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with publisher meeting the search term
     */
    private List<Book> searchBookByPublisher(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getPublisher() != null && eachBook.getPublisher().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }
        }

        return searchResults;
    }

    /**
     * Search the books by its ISBN
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with ISBN meeting the search term
     */
    private List<Book> searchBookByIsbn(List<Book> allBooks, String sT) {
        // TODO: remove spaces, commas, dashes from ISBN

        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (searchTerm.length() == 10 && eachBook.getIsbn_10() != null
                    && eachBook.getPublisher().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }

            else if (searchTerm.length() == 13 && eachBook.getIsbn_13() != null
                    && eachBook.getIsbn_13().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }
        }

        return searchResults;
    }

    /**
     * Search the books by its placement in the library
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with librarySection meeting the search term
     */
    private List<Book> searchBookByLibrarySection(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getLibrarySection() != null
                    && eachBook.getLibrarySection().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }
        }

        return searchResults;
    }

    /**
     * Search the books by their tags
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with tags meeting the search term
     */
    private List<Book> searchBookByTag(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            Iterator<String> tagIterator = eachBook.getTags().iterator();

            while (tagIterator.hasNext()) {
                String checkTag = tagIterator.next();

                if (checkTag != null && checkTag.toLowerCase().contains(searchTerm)) {
                    searchResults.add(eachBook);

                    // break from while loop as we dont want to check all tags if one already
                    // matched
                    break;
                }
            }
        }

        return searchResults;
    }

    /**
     * Search the books by their categories
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with categories meeting the search term
     */
    private List<Book> searchBookByCategory(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            Iterator<String> categoryIterator = eachBook.getCategories().iterator();

            while (categoryIterator.hasNext()) {
                String checkCategory = categoryIterator.next();

                if (checkCategory != null && checkCategory.toLowerCase().contains(searchTerm)) {
                    searchResults.add(eachBook);

                    // break from while loop as we dont want to check all categories if one already
                    // matched
                    break;
                }
            }
        }

        return searchResults;
    }

    /**
     * Search the books by its language
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with its language meeting the search term
     */
    private List<Book> searchBookByLanguage(List<Book> allBooks, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getLanguage() != null && eachBook.getLanguage().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
            }
        }

        return searchResults;
    }

    /**
     * Endpoint for GET - fetch book by id
     *
     * @param id book id
     * @return book
     */
    @GetMapping(path = "{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable("bookId") Long id) {
        List<Book> b = this.searchBookById(id);

        if (b == null) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(b.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all books meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of books meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchBook(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<Book> allBooks = this.bookRepository.findAll();

        if (searchBy == null || searchTerm == null) {
            return ResponseEntity.status(HttpStatus.OK).body(allBooks);
        }

        else if (searchBy.equals("") || searchTerm.equals("")) {
            return ResponseEntity.status(HttpStatus.OK).body(allBooks);
        }

        else if (searchBy.equalsIgnoreCase("id")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookById(Long.valueOf(searchTerm)));
        }

        else if (searchBy.equalsIgnoreCase("title")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByTitleOrSubtitle(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("author")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByAuthor(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("publisher")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByPublisher(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("isbn")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByIsbn(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("librarysection")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByLibrarySection(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("tags")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByTag(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("categories")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByCategory(allBooks, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("language")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByLanguage(allBooks, searchTerm));
        }

        else {
            String message = "Invalid book search operation";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Endpoint for POST - save the book in db
     *
     * @param b book details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book b) {
        // reset the id to 0 to prevent overwrite
        b.setBookId(0L);

        this.bookRepository.save(b);

        String message = "Book added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for PUT - update book in db
     *
     * @param b      the updated book details in json
     * @param bookId the id of the book to be updated
     * @return the message
     */
    @PutMapping(path = "{bookId}")
    public ResponseEntity<?> updateBook(@RequestBody Book b, @PathVariable("bookId") Long bookId) {
        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            b.setBookId(bookId);
            this.bookRepository.save(b);

            String message = "Book updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        else {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }
    }

    /**
     * Endpoint for DELETE - delete book from db
     *
     * @param bookId book id
     */
    @DeleteMapping(path = "{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable("bookId") Long bookId) {
        if (!this.bookRepository.existsById(bookId)) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            this.bookRepository.deleteById(bookId);
            String message = "Book deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
