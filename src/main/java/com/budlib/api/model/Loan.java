package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;

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
}
