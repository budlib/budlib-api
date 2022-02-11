package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

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
    @ManyToOne
    @JoinColumn(name = "loaner_id", foreignKey = @ForeignKey(name = "fk_trn_loaner"))
    private Loaner loaner;

    /**
     * The librarian who facilitated the transaction
     */
    @ManyToOne
    @JoinColumn(name = "librarian_id", foreignKey = @ForeignKey(name = "fk_trn_librarian"))
    private Librarian librarian;

    /**
     * The books involved in the transaction
     */
    @OneToMany(mappedBy = "transactionId")
    @JsonIgnore
    private List<TrnQuantities> bookCopies;
}
