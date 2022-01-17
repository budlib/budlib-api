package com.budlib.api;

import javax.persistence.Id;

public class Transaction {
    @Id
    private Long id = (long) -1;

    private Long bookId;
    private Long studentLId;

    private String borrowedDate;
    private String returnedDate;

    public Transaction(Long studentLid, Long bookId, String borrowedDate) {
        this.studentLId = studentLid;
        this.bookId = bookId;
        this.borrowedDate = borrowedDate;
        this.returnedDate = null;

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
     * @return String return the borrowedDate
     */
    public String getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * @param borrowedDate the borrowedDate to set
     */
    public void setBorrowedDate(String borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    /**
     * @return String return the returnedDate
     */
    public String getReturnedDate() {
        return returnedDate;
    }

    /**
     * @param returnedDate the returnedDate to set
     */
    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

}
