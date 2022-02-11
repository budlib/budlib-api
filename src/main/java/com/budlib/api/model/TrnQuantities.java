package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Represents quantity of books in the transaction
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trn_quantities")
public class TrnQuantities implements Serializable {
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
}
