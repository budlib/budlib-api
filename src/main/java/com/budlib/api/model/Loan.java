package com.budlib.api.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Outstanding books of the Loaner
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan")
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Loan ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private long loanId;

    /**
     * Loaner who borrowed books
     */
    @ManyToOne
    @JoinColumn(name = "loaner_id", foreignKey = @ForeignKey(name = "fk_loan_loaner"))
    private Loaner loaner;

    /**
     * Book which was borrowed
     */
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_loan_book"))
    private Book book;

    /**
     * Copies of the book borrowed
     */
    @Column(name = "copies")
    private int copies;

    /**
     * Date of borrowing books
     */
    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    /**
     * Expected due date of the books
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Loan [loanId=%d, bookId=%d, copies=%d, borrowDate=%s, dueDate=%s]",
                this.loanId,
                this.book.getBookId(),
                this.copies,
                this.borrowDate,
                this.dueDate);
    }
}
