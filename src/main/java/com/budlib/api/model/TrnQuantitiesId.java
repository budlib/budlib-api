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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || this.getClass() != o.getClass())
            return false;

        TrnQuantitiesId that = (TrnQuantitiesId) o;

        if (this.transaction != null ? !transaction.equals(that.transaction) : that.transaction != null)
            return false;

        return this.book != null ? book.equals(that.book) : that.book == null;
    }

    @Override
    public int hashCode() {
        int result = this.transaction != null ? this.transaction.hashCode() : 0;
        result = 31 * result + (this.book != null ? this.book.hashCode() : 0);
        return result;
    }
}
