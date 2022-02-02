package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;

/**
 * Represents quantities of the Book
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_quantity")
public class BookQuantity implements Serializable {
    /**
     * The book
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_quantity_book"))
    private Book bookId;

    /**
     * The branch of the library
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_quantity_branch"))
    private LibraryBranch branchId;

    /**
     * Total quantity at the library branch
     */
    @Column(name = "total_quantity")
    private int totalQuantity;

    /**
     * Available quantity at the library branch
     */
    @Column(name = "available_quantity")
    private int available;
}
