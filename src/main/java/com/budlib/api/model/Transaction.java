package com.budlib.api.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.budlib.api.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private static final long serialVersionUID = 1L;

    /**
     * Transaction ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trn_id")
    private long transactionId;

    /**
     * Date Time of the transaction
     */
    @Column(name = "trn_datetime")
    private ZonedDateTime transactionDateTime;

    /**
     * The type of transaction - BORROW or RETURN or EXTEND or RESERVE
     */
    @Column(name = "trn_type")
    // @Enumerated(EnumType.STRING)
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
    @OneToMany(mappedBy = "transaction")
    private List<TrnQuantities> bookCopies;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Transaction [transactionId=%d, transactionDateTime=%s, transactionType=%s, bookCopies=%s, loaner=%s, librarian=%s]",
                this.transactionId,
                this.transactionDateTime,
                this.transactionType.toString(),
                this.bookCopiesToString(),
                this.loaner,
                this.librarian);
    }

    /**
     * Prints the transaction in brief
     */
    public String toStringCompact() {
        return String.format("Transaction [transactionType=%s, loanerId=%d, librarianId=%d, bookCopies=%s]",
                this.transactionType.toString(),
                this.loaner.getLoanerId(),
                this.librarian.getLibrarianId(),
                this.bookCopiesToString());
    }

    /**
     * Converts the list of transaction quantities to a string
     *
     * @return String of transaction quantities
     */
    private String bookCopiesToString() {
        if (this.bookCopies != null) {
            StringBuilder trnQuantitiesString = new StringBuilder();

            for (int i = 0; i < this.bookCopies.size(); i++) {
                trnQuantitiesString.append(this.bookCopies.get(i).toString());
            }

            return trnQuantitiesString.toString();
        }

        return "null";
    }
}
