package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;

/**
 * Represents a branch of the library
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "branch")
public class LibraryBranch implements Serializable {
    /**
     * Internal unique ID of the branch
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "branch_id")
    private Long branchId;

    /**
     * Branch name of the library
     */
    @Column(name = "branch_name")
    private String branchName;

    /**
     * Optional branch code associated with a branch
     */
    @Column(name = "branch_code")
    private String branchCode;

    /**
     * Address of the branch
     */
    @Column(name = "branch_address")
    private String branchAddress;

    /**
     * What all book does this branch has
     */
    @OneToMany(mappedBy = "bookId")
    private List<BookQuantity> availableBooks;
}
