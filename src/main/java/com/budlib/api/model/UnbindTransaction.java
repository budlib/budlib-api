package com.budlib.api.model;

import javax.persistence.Id;

public class UnbindTransaction {
    @Id
    private Long id = (long) -1;
    private Long bookId;
    private String loaner;
    private String transactionDate;
    private String transactionType;

    public UnbindTransaction(Long id, String loaner, Long bookId, String transactionDate, String transactionType) {
        super();
        this.id = id;
        this.loaner = loaner;
        this.bookId = bookId;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
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
     * @return Long return the bookId
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * @param bookId the bookId to set
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return String return the loaner
     */
    public String getLoaner() {
        return loaner;
    }

    /**
     * @param loaner the loaner to set
     */
    public void setLoaner(String loaner) {
        this.loaner = loaner;
    }

    /**
     * @return String return the transactionDate
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate the transactionDate to set
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return String return the transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
