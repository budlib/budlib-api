package com.budlib.api.model;

import java.io.Serializable;
import lombok.*;

/**
 * Represents quantity of books in the transaction
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrnQuantitiesId implements Serializable {
    /**
     * The transaction ID
     */
    private Long transaction;

    /**
     * The books involved in the transaction
     */
    private Long book;
}
