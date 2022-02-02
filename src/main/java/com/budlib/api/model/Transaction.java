package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;

/**
 * Represents a transaction
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction implements Serializable {
    /**
     * Transaction ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trn_id")
    private long transactionId;

    /**
     * Branch at which transaction took place
     */
    @OneToOne
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_trn_branch"))
    private LibraryBranch branchId;

    /**
     * Zoned Date Time for handling branches in different time zones
     */
    @Column(name = "trn_datetime")
    private ZonedDateTime transactionDateTime;

    /**
     * The type of transaction - BORROW or RETURN or EXTEND or RESERVE
     */
    @Column(name = "trn_type")
    private TransactionType transactionType;

    /**
     * The loaner involved in the transaction
     */
    @OneToOne
    @JoinColumn(name = "loaner_id", foreignKey = @ForeignKey(name = "fk_trn_loaner"))
    private Loaner loanerId;

    /**
     * The coordinator who facilitated the transaction
     */
    @OneToOne
    @JoinColumn(name = "coordinator_id", foreignKey = @ForeignKey(name = "fk_trn_coordinator"))
    private Librarian librarianId;

    /**
     * The books involved in the transaction
     */
    @OneToMany(mappedBy = "transactionId")
    private List<TrnQuantities> bookCopies;
}
