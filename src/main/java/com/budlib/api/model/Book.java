package com.budlib.api.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a book in the library
 */
public class Book {
    /**
     * Internal unique ID of the book
     */
    @Id
    private Long bookId;

    /**
     * Title of the book
     */
    private String title;

    /**
     * Subtitle of the book
     */
    private String subtitle;

    /**
     * List of authors of the book
     */
    private List<String> authors;

    /**
     * Publishing company of the book
     */
    private String publisher;

    /**
     * Edition number of the book
     */
    private String edition;

    /**
     * Year of publishing
     */
    private String year;

    /**
     * ISBN 10 of the book
     */
    private String isbn_10;

    /**
     * ISBN 13 of the book
     */
    private String isbn_13;

    /**
     * Section in the library branch where the book is usually placed
     */
    private String librarySection;

    /**
     * Any particular notes related to the book goes here
     */
    private String notes;

    /**
     * Total quantity of the book at the library branch
     */
    private int qty;

    /**
     * Available quantity at the library branch
     */
    private int available;

    /**
     * Tags related to the book - like
     * TODO: think of tags
     */
    private List<String> tags;

    /**
     * Categories related to the book - like
     * TODO: think of categories
     */
    private List<String> categories;

    /**
     * URL of thumbnail the book
     */
    private String imageLink;

    /**
     * Language of the book
     */
    private String language;

    /**
     * Retail price of the book
     */
    private double retailPrice;

    /**
     * Price of the book at which it was procured in the library. For example, tt
     * can be kept as $0.00 for the books that were donated to the library
     */
    private double internalLibraryPrice;

    /**
     * Online buying link on amazon.ca
     * TODO: check if its datatype can be changed
     */
    private String amazonCaLink;

    // add later
    /*
     * reserving feature
     */

    /**
     * Private constructor to initialize empty lists
     */
    private Book() {
        this.authors = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public Book(Long bookId, String title, String subtitle, List<String> authors, String publisher, String edition,
            String year, String isbn_10, String isbn_13, String librarySection, String notes, int qty, int available,
            List<String> tags, List<String> categories, String imageLink, String language, double retailPrice,
            double internalLibraryPrice, String amazonCaLink) {
        this();
        this.bookId = bookId;
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.edition = edition;
        this.year = year;
        this.isbn_10 = isbn_10;
        this.isbn_13 = isbn_13;
        this.librarySection = librarySection;
        this.notes = notes;
        this.qty = qty;
        this.available = available;
        this.tags = tags;
        this.categories = categories;
        this.imageLink = imageLink;
        this.language = language;
        this.retailPrice = retailPrice;
        this.internalLibraryPrice = internalLibraryPrice;
        this.amazonCaLink = amazonCaLink;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getAuthors() {
        return this.authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return this.edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIsbn_10() {
        return this.isbn_10;
    }

    public void setIsbn_10(String isbn_10) {
        this.isbn_10 = isbn_10;
    }

    public String getIsbn_13() {
        return this.isbn_13;
    }

    public void setIsbn_13(String isbn_13) {
        this.isbn_13 = isbn_13;
    }

    public String getLibrarySection() {
        return this.librarySection;
    }

    public void setLibrarySection(String librarySection) {
        this.librarySection = librarySection;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getQty() {
        return this.qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getImageLink() {
        return this.imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getRetailPrice() {
        return this.retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getInternalLibraryPrice() {
        return this.internalLibraryPrice;
    }

    public void setInternalLibraryPrice(double internalLibraryPrice) {
        this.internalLibraryPrice = internalLibraryPrice;
    }

    public String getAmazonCaLink() {
        return this.amazonCaLink;
    }

    public void setAmazonCaLink(String amazonCaLink) {
        this.amazonCaLink = amazonCaLink;
    }
}
