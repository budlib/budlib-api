package com.budlib.api.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.budlib.api.enums.LibrarySection;
import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Book;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Tag;
import com.budlib.api.repository.BookRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for {@link Book}
 */
@Service
public class BookService {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookService.class);

    /**
     * {@link TagService} for interacting with Tags
     */
    private final TagService tagService;

    /**
     * {@link BookRepository} for interacting with database
     */
    private final BookRepository bookRepository;

    /**
     * Constructor
     *
     * @param br {@code BookRepository} for database interaction with Book
     * @param ts {@code TagService} for interaction with Tags
     */
    @Autowired
    public BookService(final BookRepository br, final TagService ts) {

        LOGGER.debug("BookService: injected BookRepository and TagService");

        this.bookRepository = br;
        this.tagService = ts;
    }

    /**
     * Get {@link Book} by ID
     *
     * @param bookId ID of the Book
     * @return Book
     */
    public Book getBookById(final Long bookId) {

        LOGGER.info("getBookById: bookId = {}", bookId);

        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            return bookOptional.get();
        }

        else {
            LOGGER.warn("Book with ID={} not found", bookId);
            return null;
        }
    }

    /**
     * Get list of {@link Tag} of a {@link Book} by Book ID
     *
     * @param bookId ID of the Book
     * @return list of Tags
     * @throws NotFoundException
     */
    public List<Tag> getBookTags(final Long bookId) throws NotFoundException {

        LOGGER.info("getBookTags: bookId = {}", bookId);

        Book b = this.getBookById(bookId);

        if (b == null) {
            LOGGER.error("Book with ID={} not found", bookId);
            throw new NotFoundException(String.format("Book with ID=%d not found", bookId));
        }

        else {
            return b.getTags();
        }
    }

    /**
     * Get list of current {@link Loan} of a {@link Book} by Book ID
     *
     * @param bookId ID of the Book
     * @return list of Loans
     * @throws NotFoundException
     */
    public List<Loan> getCurrentLoans(final Long bookId) throws NotFoundException {

        LOGGER.info("getCurrentLoans: bookId = {}", bookId);

        Book b = this.getBookById(bookId);

        if (b == null) {
            LOGGER.error("Book with ID={} not found", bookId);
            throw new NotFoundException(String.format("Book with ID=%d not found", bookId));
        }

        else {
            return b.getCurrentLoans();
        }
    }

    /**
     * Search {@link Book} in the database
     *
     * @param searchParameters parameters to filter the search
     * @return list of Books that match the search parameters
     */
    public List<Book> searchBook(final Map<String, String> searchParameters) {

        LOGGER.info("searchBook");

        StringBuilder sb = new StringBuilder();
        searchParameters.forEach((k, v) -> sb.append(k + "=" + v + ", "));

        LOGGER.info("searchBook: parameters = {}", sb.toString());

        List<Book> allBooks = this.getAllBooks();

        if (searchParameters.isEmpty()) {
            LOGGER.debug("No search parameters to filter books");
            return allBooks;
        }

        Set<String> suppliedKeys = searchParameters.keySet();
        LOGGER.debug("{} number of parameters supplied for filtering", suppliedKeys.size());

        Iterator<String> itr = suppliedKeys.iterator();

        while (itr.hasNext()) {
            String key = itr.next();

            switch (key) {
                case "title":
                    LOGGER.debug("Filtering by title");
                    allBooks = this.filterBooksByTitle(allBooks, searchParameters.get(key));
                    break;

                case "subtitle":
                    LOGGER.debug("Filtering by subtitle");
                    allBooks = this.filterBooksBySubtitle(allBooks, searchParameters.get(key));
                    break;

                case "author":
                    LOGGER.debug("Filtering by author");
                    allBooks = this.filterBooksByAuthor(allBooks, searchParameters.get(key));
                    break;

                case "publisher":
                    LOGGER.debug("Filtering by publisher");
                    allBooks = this.filterBooksByPublisher(allBooks, searchParameters.get(key));
                    break;

                case "isbn":
                    LOGGER.debug("Filtering by ISBN");
                    allBooks = this.filterBooksByIsbn(allBooks, searchParameters.get(key));
                    break;

                case "librarysection":
                    LOGGER.debug("Filtering by librarysection");
                    allBooks = this.filterBooksByLibrarySection(allBooks, searchParameters.get(key));
                    break;

                case "tags":
                    LOGGER.debug("Filtering by tags");
                    allBooks = this.filterBooksByTag(allBooks, searchParameters.get(key));
                    break;

                case "language":
                    LOGGER.debug("Filtering by language");
                    allBooks = this.filterBooksByLanguage(allBooks, searchParameters.get(key));
                    break;
            }
        }

        return allBooks;
    }

    /**
     * Save the {@link Book} in the database
     *
     * @param book Book details
     * @return saved Book
     * @throws UserInputException
     */
    public Book addBook(final Book book) throws UserInputException {

        LOGGER.info("addBook: book = {}", book);

        // reset the id to 0 to prevent overwrite
        book.setBookId(0L);

        if (book.getTotalQuantity() != book.getAvailableQuantity()) {
            LOGGER.error("Total quantity and available quantity should be equal when adding new books");
            throw new UserInputException("Total quantity and available quantity should be equal when adding new books");
        }

        if (book.getLibrarySection() == null) {
            LOGGER.warn("No library section provided for book. Defaulting to CHILDREN_LIBRARY");
            book.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
        }

        List<Tag> suppliedTagList = book.getTags();
        List<Tag> uniqueTagList = new ArrayList<>();

        if (suppliedTagList != null) {
            for (Tag eachTag : suppliedTagList) {
                Tag t = this.tagService.addTag(eachTag);
                uniqueTagList.add(t);
            }
        }

        book.setTags(uniqueTagList);
        return this.bookRepository.save(book);
    }

    /**
     * Update {@link Book} in the database
     *
     * @param book   updated Book details
     * @param bookId ID of the Book to be updated
     * @return updated Book
     * @throws NotFoundException
     * @throws UserInputException
     */
    public Book updateBook(final Book book, final Long bookId) throws NotFoundException, UserInputException {

        LOGGER.info("updateBook: book = {}, bookId = {}", book, bookId);

        Book b = this.getBookById(bookId);

        if (b == null) {
            LOGGER.error("Book with ID={} not found", bookId);
            throw new NotFoundException(String.format("Book with ID=%d not found", bookId));
        }

        else {
            book.setBookId(bookId);

            int totalQty = book.getTotalQuantity();
            int availableQty = book.getAvailableQuantity();

            // fetch loanQty from database rather than from the supplied book object
            int loanQty = this.getLoanCountOfBook(b);

            if (totalQty != availableQty + loanQty) {
                LOGGER.error(
                        "Invalid values for total quantity or available quantity. Total ({}) should match sum of available ({}) and loan count ({})",
                        totalQty, availableQty, loanQty);

                throw new UserInputException(String.format(
                        "Invalid values for total quantity or available quantity.%nTotal (%d) should match sum of available (%d) and loan count (%d)",
                        totalQty, availableQty, loanQty));
            }

            if (book.getLibrarySection() == null) {
                LOGGER.warn("No library section provided for book. Defaulting to CHILDREN_LIBRARY");
                book.setLibrarySection(LibrarySection.CHILDREN_LIBRARY);
            }

            List<Tag> suppliedTagList = book.getTags();
            List<Tag> uniqueTagList = new ArrayList<>();

            for (Tag eachTag : suppliedTagList) {
                Tag t = this.tagService.addTag(eachTag);
                uniqueTagList.add(t);
            }

            book.setTags(uniqueTagList);
            return this.bookRepository.save(book);
        }
    }

    /**
     * Delete {@link Book} from the database
     *
     * @param bookId ID of the Book to be deleted
     * @throws NotFoundException
     * @throws UserInputException
     */
    public void deleteBook(final Long bookId) throws NotFoundException, UserInputException {

        LOGGER.info("deleteBook: bookId = {}", bookId);

        Book toBeDeleted = this.getBookById(bookId);

        if (toBeDeleted == null) {
            LOGGER.error("Book with ID={} not found", bookId);
            throw new NotFoundException(String.format("Book with ID=%d not found", bookId));
        }

        else if (toBeDeleted.getCurrentLoans().size() != 0) {
            LOGGER.error("Cannot delete book with outstanding loans");
            throw new UserInputException("Cannot delete book with outstanding loans");
        }

        else {
            this.bookRepository.deleteById(bookId);
        }
    }

    /**
     * Import the CSV file that was uploaded earlier, with the help of fields
     * mapping provided
     *
     * @param bookCsv the fields mapping
     * @return number of books imported
     * @throws FileNotFoundException
     * @throws UserInputException
     * @throws IOException
     * @throws NullPointerException
     * @throws Throwable
     */
    public int importBooks(final Map<String, String> bookCsv)
            throws FileNotFoundException, UserInputException, IOException, NullPointerException, Throwable {

        LOGGER.info("importBooks: bookCsv map = {}", bookCsv);

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

            return countImported;
        }

        catch (FileNotFoundException e) {
            LOGGER.error("Error in retrieving uploaded file");
            throw new FileNotFoundException("Error in retrieving uploaded file");
        }

        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Incorrect mapping provided");
            throw new UserInputException("Incorrect mapping provided");
        }

        catch (IOException e) {
            LOGGER.error("Error in parsing uploaded file");
            throw new IOException("Error in parsing uploaded file");
        }

        catch (NullPointerException e) {
            LOGGER.error("All nulls are not handled");
            throw new NullPointerException("All nulls are not handled");
        }

        catch (Throwable e) {
            LOGGER.error("Something unexpected happened", e);
            throw new Throwable("Something unexpected happened");
        }
    }

    /**
     * Get the list of all {@link Book} in the database
     *
     * @return list of all Books
     */
    private List<Book> getAllBooks() {
        LOGGER.debug("getAllBooks");
        return this.bookRepository.findAll();
    }

    /**
     * Filter given list of {@link Book} by their title
     *
     * @param allBooks list of Book to filter
     * @param filter   title to filter
     * @return filtered list of Books containing the filter string in their title
     */
    private List<Book> filterBooksByTitle(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByTitle: filter = {}", filter);

        String givenTitle = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getTitle() != null && eachBook.getTitle().toLowerCase().contains(givenTitle)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their subtitle
     *
     * @param allBooks list of Book to filter
     * @param filter   subtitle to filter
     * @return filtered list of Books containing the filter string in their subtitle
     */
    private List<Book> filterBooksBySubtitle(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksBySubtitle: filter = {}", filter);

        String givenSubtitle = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getSubtitle() != null && eachBook.getSubtitle().toLowerCase().contains(givenSubtitle)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their authors
     *
     * @param allBooks list of Book to filter
     * @param filter   author to filter
     * @return filtered list of Books containing the filter string in their authors
     */
    private List<Book> filterBooksByAuthor(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByAuthor: filter = {}", filter);

        String givenAuthor = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getAuthors() != null && eachBook.getAuthors().toLowerCase().contains(givenAuthor)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their publisher
     *
     * @param allBooks list of Book to filter
     * @param filter   publisher to filter
     * @return filtered list of Books containing the filter string in their
     *         publisher
     */
    private List<Book> filterBooksByPublisher(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByPublisher: filter = {}", filter);

        String givenPublisher = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getPublisher() != null && eachBook.getPublisher().toLowerCase().contains(givenPublisher)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their ISBN
     *
     * @param allBooks list of Book to filter
     * @param filter   ISBN to filter
     * @return filtered list of Books containing the filter string in their ISBN
     */
    private List<Book> filterBooksByIsbn(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByIsbn: filter = {}", filter);

        String givenIsbn = filter.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (givenIsbn.length() == 10 && eachBook.getIsbn_10() != null
                    && eachBook.getIsbn_10().toLowerCase().contains(givenIsbn)) {
                filteredResults.add(eachBook);
            }

            else if (givenIsbn.length() == 13 && eachBook.getIsbn_13() != null
                    && eachBook.getIsbn_13().toLowerCase().contains(givenIsbn)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their {@link LibrarySection}
     *
     * @param allBooks list of Book to filter
     * @param filter   LibrarySection to filter
     * @return filtered list of Books containing the filter string in their
     *         LibrarySection
     */
    private List<Book> filterBooksByLibrarySection(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByLibrarySection: filter = {}", filter);

        String givenLibrarySection = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getLibrarySection() != null
                    && eachBook.getLibrarySection().toString().toLowerCase().contains(givenLibrarySection)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their {@link Tag}
     *
     * @param allBooks list of Book to filter
     * @param filter   Tag to filter
     * @return filtered list of Books containing the filter string in their Tag
     */
    private List<Book> filterBooksByTag(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByTag: filter = {}", filter);

        String givenTag = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            Iterator<Tag> tagIterator = eachBook.getTags().iterator();

            while (tagIterator.hasNext()) {
                Tag checkTag = tagIterator.next();

                if (checkTag != null && checkTag.getTagName().toLowerCase().contains(givenTag)) {
                    filteredResults.add(eachBook);

                    // break from while loop as we don't want to check all tags if one already
                    // matched
                    break;
                }
            }
        }

        return filteredResults;
    }

    /**
     * Filter given list of {@link Book} by their language
     *
     * @param allBooks list of Book to filter
     * @param filter   language to filter
     * @return filtered list of Books containing the filter string in their language
     */
    private List<Book> filterBooksByLanguage(final List<Book> allBooks, final String filter) {

        LOGGER.debug("filterBooksByLanguage: filter = {}", filter);

        String givenLanguage = filter.toLowerCase();
        List<Book> filteredResults = new ArrayList<>();

        for (Book eachBook : allBooks) {
            if (eachBook.getLanguage() != null && eachBook.getLanguage().toLowerCase().contains(givenLanguage)) {
                filteredResults.add(eachBook);
            }
        }

        return filteredResults;
    }

    /**
     * Verifies if the CSV record if empty or not. A record is empty when all the
     * intended details are blank.
     *
     * @param record The CSV record to check for emptiness
     * @return {@code true} if empty, {@code false} false otherwise
     */
    private boolean checkEmptyRecord(final CSVRecord record) {

        LOGGER.debug("checkEmptyRecord: record = {}", record);

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
     * Converts CSV record to {@link Book}, with the field mapping provided by
     * bookCsv
     *
     * @param record  The CSV Record fetched from the CSV file
     * @param bookCsv Map of fields
     * @return Book object
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    private Book csvRecordToBook(final CSVRecord record, final Map<String, String> bookCsv)
            throws NumberFormatException, NullPointerException {

        LOGGER.debug("csvRecordToBook: record = {}, bookCsv map = {}", record, bookCsv);

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
        if (subtitle != null) {
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
                    Tag t = this.tagService.addTag(eachTag);
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

    /**
     * Calculate the total number of outstanding copies of given {@link Book}
     *
     * @param b Book for which total number of outstanding copies is required
     * @return total number of outstanding copies
     */
    private int getLoanCountOfBook(final Book b) {

        LOGGER.debug("getLoanCountOfBook: book = {}", b);

        List<Loan> currentLoans = b.getCurrentLoans();

        int count = 0;

        for (Loan eachLoan : currentLoans) {
            count = count + eachLoan.getCopies();
        }

        return count;
    }
}
