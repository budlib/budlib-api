package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrnQuantities implements Serializable {
    /**
     * The transaction ID
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "trn_id", foreignKey = @ForeignKey(name = "fk_trnquan_trn"))
    private Transaction transactionId;

    /**
     * The books involved in the transaction
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_trnquan_book"))
    private Book bookId;

    /**
     * Copies of the books exchanging hands. To be kept
     * +1 for BORROW
     * -1 for RETURN
     * 0 for EXTEND/RESERVE
     */
    @Column(name = "copies")
    private int copies;
}
