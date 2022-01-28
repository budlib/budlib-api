package com.budlib.api.model;

import javax.persistence.Id;

public class Transaction {
    @Id
    private Long id = (long) -1;
    private Long bookId;
    private Long studentLId;
    private String transactionDate;
    private String transactionType;

    public Transaction(Long id, Long studentLId, Long bookId, String transactionDate, String transactionType) {
        super();
        this.id = id;
        this.studentLId = studentLId;
        this.bookId = bookId;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
    }

    /**
     * @return String return the id
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
     * @return Long return the studentLId
     */
    public Long getStudentLId() {
        return studentLId;
    }

    /**
     * @param studentLId the studentLId to set
     */
    public void setStudentLId(Long studentLId) {
        this.studentLId = studentLId;
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
