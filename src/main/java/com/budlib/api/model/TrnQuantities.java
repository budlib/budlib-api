package com.budlib.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents quantity of books in the transaction
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@IdClass(TrnQuantitiesId.class)
@Table(name = "trn_quantities")
public class TrnQuantities implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The transaction ID
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "trn_id", foreignKey = @ForeignKey(name = "fk_trnqty_trn"))
    @JsonIgnore
    private Transaction transaction;

    /**
     * The books involved in the transaction
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_trnqty_book"))
    private Book book;

    /**
     * Copies of the books exchanging hands
     */
    @Column(name = "copies")
    private int copies;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%nTrnQuantities [bookId=%d, title=\"%s\", copies=%d]",
                this.book.getBookId(),
                this.book.getTitle(),
                this.copies);
    }
}
