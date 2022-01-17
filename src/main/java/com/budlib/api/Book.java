package com.budlib.api;

import javax.persistence.Id;

public class Book {
    @Id
    private Long id = (long) -1;

    private String title;
    private String authors;
    private String publisher;
    private String year;
    private String ISBN;
    private Boolean loaned_out;

    private String edition;

    public Book(Long id, String ISBN, String title, String authors, String publisher, String year,
            String edition) {
        super();
        this.ISBN = ISBN;
        this.id = id;

        this.title = title;
        this.authors = authors;

        this.publisher = publisher;
        this.year = year;
        this.edition = edition;
        this.loaned_out = false;

    }

    /**
     * @return Long return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return String return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return String return the authors
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * @param authors the authors to set
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * @return String return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return String return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return String return the ISBN
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * @param ISBN the ISBN to set
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * @return String return the edition
     */
    public String getEdition() {
        return edition;
    }

    /**
     * @param edition the edition to set
     */
    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * @return Boolean return the loaned_out
     */
    public Boolean isLoaned_out() {
        return loaned_out;
    }

    /**
     * @param loaned_out the loaned_out to set
     */
    public void setLoaned_out(Boolean loaned_out) {
        this.loaned_out = loaned_out;
    }

}
