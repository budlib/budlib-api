package com.budlib.api;

import javax.persistence.Id;

public class Book {
    @Id
    private Long id = (long) -1;

    private String title;
    private String authors;
    private String language;
    private String categories;
    private double averageRating;
    private String maturityRating;
    private String publisher;
    private String publishedDate;
    private int pageCount;
    private String ISBN;

    public Book(Long id, String title, String authors, String language, String categories, double averageRating,
            String maturityRating,
            String publisher, String publishedDate, int pageCount, String ISBN) {
        super();

        this.id = id;

        this.title = title;
        this.authors = authors;
        this.language = language;
        this.categories = categories;
        this.averageRating = averageRating;
        this.maturityRating = maturityRating;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.ISBN = ISBN;
    }

    public Long getId() {
        return id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getMaturityRating() {
        return maturityRating;
    }

    public void setMaturityRating(String maturityRating) {
        this.maturityRating = maturityRating;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

}
