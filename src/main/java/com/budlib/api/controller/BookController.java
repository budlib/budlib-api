package com.budlib.api.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.budlib.api.enums.LibrarySection;
import com.budlib.api.model.Book;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Tag;
import com.budlib.api.repository.BookRepository;
import com.budlib.api.repository.TagRepository;
import com.budlib.api.response.ErrorBody;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
 * Controller for books
 */
@CrossOrigin
@RestController
@RequestMapping("api/books")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private final BookRepository bookRepository;
    private final TagRepository tagRepository;

    @Autowired
    public BookController(final BookRepository br, final TagRepository tr) {

        LOGGER.debug("BookController");

        this.bookRepository = br;
        this.tagRepository = tr;
    }

    /**
     * Search the books by id
     *
     * @param id book id
     * @return list of books with id; the list would have utmost one element
     */
    private List<Book> searchBookById(Long id) {

        LOGGER.info("searchBookById: bookId = {}", id);

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

        LOGGER.info("searchBookByTitleOrSubtitle: searchTerm = {}", sT);

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

        LOGGER.info("searchBookByAuthor: searchTerm = {}", sT);

        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getAuthors() != null && eachBook.getAuthors().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBook);
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

        LOGGER.info("searchBookByPublisher: searchTerm = {}", sT);

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

        LOGGER.info("searchBookByIsbn: searchTerm = {}", sT);

        String searchTerm = sT.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (searchTerm.length() == 10 && eachBook.getIsbn_10() != null
                    && eachBook.getIsbn_10().toLowerCase().contains(searchTerm)) {
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

        LOGGER.info("searchBookByLibrarySection: searchTerm = {}", sT);

        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        try {
            for (Book eachBook : allBooks) {
                if (eachBook.getLibrarySection() != null
                        && eachBook.getLibrarySection().toString().toLowerCase().contains(searchTerm)) {
                    searchResults.add(eachBook);
                }
            }

            return searchResults;
        }

        // TODO: not actually thrown, cleanup required
        catch (IllegalArgumentException e) {
            return searchResults;
        }
    }

    /**
     * Search the books by their tags
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with tags meeting the search term
     */
    private List<Book> searchBookByTag(List<Book> allBooks, String sT) {

        LOGGER.info("searchBookByTag: searchTerm = {}", sT);

        String searchTerm = sT.toLowerCase();
        List<Book> searchResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            Iterator<Tag> tagIterator = eachBook.getTags().iterator();

            while (tagIterator.hasNext()) {
                Tag checkTag = tagIterator.next();

                if (checkTag != null && checkTag.getTagName().toLowerCase().contains(searchTerm)) {
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
     * Search the books by its language
     *
     * @param allBooks list of all books
     * @param sT       search term
     * @return filtered list of book with its language meeting the search term
     */
    private List<Book> searchBookByLanguage(List<Book> allBooks, String sT) {

        LOGGER.info("searchBookByLanguage: searchTerm = {}", sT);

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

        LOGGER.info("getBookById: bookId = {}", id);

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
     * Endpoint for GET - fetch tags of a book by id
     *
     * @param id book id
     * @return tag list
     */
    @GetMapping(path = "{bookId}/tags")
    public ResponseEntity<?> getBookTags(@PathVariable("bookId") Long id) {

        LOGGER.info("getBookTags: bookId = {}", id);

        List<Book> b = this.searchBookById(id);

        if (b == null) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(b.get(0).getTags());
        }
    }

    /**
     * Endpoint for GET - fetch the current loans of the book
     *
     * @param id book ID
     * @return list of loans
     */
    @GetMapping(path = "{bookId}/loans")
    public ResponseEntity<?> getCurrentLoans(@PathVariable("bookId") Long id) {

        LOGGER.info("getCurrentLoans: bookId = {}", id);

        List<Book> b = this.searchBookById(id);

        if (b == null) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            List<Loan> currentLoans = b.get(0).getCurrentLoans();
            return ResponseEntity.status(HttpStatus.OK).body(currentLoans);
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

        LOGGER.info("searchBook: searchBy = {}, searchTerm = {}", searchBy, searchTerm);

        List<Book> allBooks = this.bookRepository.findAll();

        try {
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
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.searchBookByTitleOrSubtitle(allBooks, searchTerm));
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

            else if (searchBy.equalsIgnoreCase("language")) {
                return ResponseEntity.status(HttpStatus.OK).body(this.searchBookByLanguage(allBooks, searchTerm));
            }

            else {
                // String message = "Invalid book search operation";
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                // .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                return ResponseEntity.status(HttpStatus.OK).body(allBooks);
            }
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(allBooks);
        }
    }

    /**
     * Saves and returns and tag of the book, while handling duplicates
     *
     * @param t Tag to be saved
     * @return Tag saved
     */
    private Tag findUniqueTag(Tag t) {

        LOGGER.info("findUniqueTag: tag = {}", t);

        // reset the id to 0 to prevent overwrite
        t.setTagId(0L);

        List<Tag> allTags = this.tagRepository.findAll();

        for (Tag eachTag : allTags) {
            if (eachTag.getTagName().equalsIgnoreCase((t.getTagName()))) {
                return eachTag;
            }
        }

        return this.tagRepository.save(t);
    }

    /**
     * Endpoint for POST - save the book in db
     *
     * @param b book details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book b) {

        LOGGER.info("addBook: book = {}", b);

        // reset the id to 0 to prevent overwrite
        b.setBookId(0L);

        if (b.getTotalQuantity() != b.getAvailableQuantity()) {
            String message = "Total quantity and available quantity should be equal when adding new books";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }

        if (b.getLibrarySection() == null) {
            b.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
        }

        List<Tag> suppliedTagList = b.getTags();
        List<Tag> uniqueTagList = new ArrayList<>();

        if (suppliedTagList != null) {
            for (Tag eachTag : suppliedTagList) {
                Tag t = this.findUniqueTag(eachTag);
                uniqueTagList.add(t);
            }
        }

        b.setTags(uniqueTagList);
        this.bookRepository.save(b);

        String message = "Book added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for POST - import multiple books in db
     *
     * @param bookList list of book details in json
     * @return the message
     */
    @PostMapping(path = "import")
    public ResponseEntity<?> importBooks(@RequestBody List<Book> bookList) {

        LOGGER.info("importBooks: bookList = {}", bookList);

        boolean flag = false;
        int countNotImported = 0;
        int countImported = 0;

        for (Book eachBook : bookList) {
            // reset the id to 0 to prevent overwrite
            eachBook.setBookId(0L);

            if (eachBook.getLibrarySection() == null) {
                eachBook.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
            }

            if (eachBook.getTotalQuantity() != eachBook.getAvailableQuantity()) {
                flag = true;
                countNotImported++;
            }

            else {
                this.bookRepository.save(eachBook);
                countImported++;
            }
        }

        String message = String.format("%d books imported successfully.", countImported);

        if (flag) {
            message += String.format("\n%d books not imported.", countNotImported);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    private boolean checkEmptyRecord(CSVRecord record) {

        LOGGER.info("checkEmptyRecord: record = {}", record);

        Iterator<String> itr = record.iterator();

        while (itr.hasNext()) {
            String str = itr.next();

            if (str != null && str.length() != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts CSV record to Book object, with the field mapping provided by
     * bookCsv
     *
     * @param record  The CSV Record fetched from the CSV file
     * @param bookCsv
     * @return
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    private Book csvRecordToBook(final CSVRecord record, HashMap<String, String> bookCsv)
            throws NumberFormatException, NullPointerException {

        LOGGER.info("csvRecordToBook: record = {}, bookCsv map = {}", record, bookCsv);

        if (this.checkEmptyRecord(record)) {
            return null;
        }

        Book b = new Book();
        b.setBookId(0);

        // all the below are actually integers in String format
        String title = bookCsv.get("title");
        String subtitle = bookCsv.get("subtitle");
        String authors = bookCsv.get("authors");
        String publisher = bookCsv.get("publisher");
        String edition = bookCsv.get("edition");
        String year = bookCsv.get("year");
        String language = bookCsv.get("language");
        String isbn_10 = bookCsv.get("isbn_10");
        String isbn_13 = bookCsv.get("isbn_13");
        String librarySection = bookCsv.get("librarySection");
        String totalQuantity = bookCsv.get("totalQuantity");
        String notes = bookCsv.get("notes");
        String tags = bookCsv.get("tags");
        String imageLink = bookCsv.get("imageLink");
        String priceRetail = bookCsv.get("priceRetail");
        String priceLibrary = bookCsv.get("priceLibrary");

        // set title
        if (title != null) {
            b.setTitle(record.get(Integer.parseInt(title)));
        }

        // set subtitle
        if(subtitle != null) {
            b.setSubtitle(record.get(Integer.parseInt(subtitle)));
        }

        // set authors
        if (authors != null) {
            b.setAuthors(record.get(Integer.parseInt(authors)));
        }

        // set publisher
        if (publisher != null) {
            b.setPublisher(record.get(Integer.parseInt(publisher)));
        }

        // set edition
        if (edition != null) {
            b.setEdition(record.get(Integer.parseInt(edition)));
        }

        // set year
        if (year != null) {
            b.setYear(record.get(Integer.parseInt(year)));
        }

        // set language
        if (language != null) {
            b.setLanguage(record.get(Integer.parseInt(language)));
        }

        // set ISBN
        if (isbn_10 != null) {
            if (Book.cleanIsbn(record.get(Integer.parseInt(isbn_10))).length() > 10) {
                b.setIsbn_13(record.get(Integer.parseInt(isbn_10)));
            }

            else {
                b.setIsbn_10(record.get(Integer.parseInt(isbn_10)));
            }
        }

        if (isbn_13 != null) {
            if (Book.cleanIsbn(record.get(Integer.parseInt(isbn_13))).length() > 10) {
                b.setIsbn_13(record.get(Integer.parseInt(isbn_13)));
            }

            else {
                b.setIsbn_10(record.get(Integer.parseInt(isbn_13)));
            }
        }

        // set library section
        if (librarySection == null) {
            b.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
        }

        else {
            try {
                b.setLibrarySection(LibrarySection.valueOf(record.get(librarySection)));
            }

            catch (IllegalArgumentException e) {
                LOGGER.debug("csvRecordToBook: provided library section = {}", record.get(librarySection));
                LOGGER.warn("csvRecordToBook: Could not set library section. Defaulting to CHILDREN_LIBRARY");
                b.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
            }
        }

        // set total quantity
        if (totalQuantity == null) {
            LOGGER.warn("csvRecordToBook: Value not provided. Defaulting quantity to 1");
            b.setTotalQuantity(1);
            b.setAvailableQuantity(1);
        }

        else {
            b.setTotalQuantity(Integer.parseInt(record.get(Integer.parseInt(totalQuantity))));
            b.setAvailableQuantity(b.getTotalQuantity());
        }

        // set notes
        if (notes != null) {
            b.setNotes(record.get(Integer.parseInt(notes)));
        }

        // set the tags
        if (tags != null) {
            String[] tagStrings = record.get(Integer.parseInt(tags)).split(",");
            List<Tag> suppliedTagList = new ArrayList<>();

            for (String ts : tagStrings) {
                suppliedTagList.add(new Tag(ts.trim()));
            }

            List<Tag> uniqueTagList = new ArrayList<>();

            if (suppliedTagList.size() != 0) {
                for (Tag eachTag : suppliedTagList) {
                    Tag t = this.findUniqueTag(eachTag);
                    uniqueTagList.add(t);
                }
            }

            b.setTags(uniqueTagList);
        }

        // set image link
        if (imageLink != null) {
            b.setImageLink(record.get(Integer.parseInt(imageLink)));
        }

        // set retail price
        if (priceRetail != null) {
            b.setPriceRetail(Double.parseDouble(record.get(Integer.parseInt(priceRetail))));
        }

        // set library price
        if (priceLibrary != null) {
            b.setPriceLibrary(Double.parseDouble(record.get(Integer.parseInt(priceLibrary))));
        }

        return b;
    }

    @PostMapping(path = "importUpdated")
    public ResponseEntity<?> importBooksUpdated(@RequestBody HashMap<String, String> bookCsv) {

        LOGGER.info("csvRecordToBook: bookCsv map = {}", bookCsv);

        int countImported = 0;
        final String uploadCSVFilePath = bookCsv.get("filename");

        // remove the filename as its not needed anymore, and to prevent hindrance
        bookCsv.remove("filename");

        try {
            FileReader fileReader = new FileReader(uploadCSVFilePath);

            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();

            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            Iterator<CSVRecord> itr = csvParser.iterator();

            while (itr.hasNext()) {
                CSVRecord record = itr.next();

                Book b = this.csvRecordToBook(record, bookCsv);

                if (b == null) {
                    continue;
                }

                else {
                    this.bookRepository.save(b);
                    countImported++;
                }
            }

            csvParser.close();

            String message = String.format("%d books imported successfully", countImported);
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));

        }

        catch (FileNotFoundException e) {
            String message = "Error in retrieving uploaded file";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            String message = "React Coding issue: Incorrect mapping provided";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (IOException e) {
            String message = "Error in parsing uploaded file";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (NullPointerException e) {
            String message = "Coding issue: All nulls are not handled";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (Exception e) {
            String message = "Something unexpected happened";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }
    }

    /**
     * Calculate the total number of outstanding copies of given book
     *
     * @param b Book for which total number of outstanding copies is required
     * @return total number of outstanding copies
     */
    private int getLoanCountOfBook(Book b) {

        LOGGER.info("getLoanCountOfBook: book = {}", b);

        List<Loan> currentLoans = b.getCurrentLoans();

        int count = 0;

        for (Loan eachLoan : currentLoans) {
            count = count + eachLoan.getCopies();
        }

        return count;
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

        LOGGER.info("updateBook: book = {}, bookId = {}", b, bookId);

        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            b.setBookId(bookId);

            int totalQty = b.getTotalQuantity();
            int availableQty = b.getAvailableQuantity();
            int loanQty = this.getLoanCountOfBook(bookOptional.get());

            if (totalQty != availableQty + loanQty) {
                String message = String.format(
                        "Invalid values for total quantity or available quantity.%nTotal (%d) should match sum of available (%d) and loan count (%d)",
                        totalQty, availableQty, loanQty);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            if (b.getLibrarySection() == null) {
                b.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
            }

            List<Tag> suppliedTagList = b.getTags();
            List<Tag> uniqueTagList = new ArrayList<>();

            for (Tag eachTag : suppliedTagList) {
                Tag t = this.findUniqueTag(eachTag);
                uniqueTagList.add(t);
            }

            b.setTags(uniqueTagList);
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

        LOGGER.info("deleteBook: bookId = {}", bookId);

        if (!this.bookRepository.existsById(bookId)) {
            String message = "Book not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            Book toBeDeleted = this.bookRepository.getById(bookId);

            if (toBeDeleted.getCurrentLoans().size() != 0) {
                String message = "Cannot delete book with outstanding loans";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            this.bookRepository.deleteById(bookId);
            String message = "Book deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
