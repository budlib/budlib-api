package com.budlib.api.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents quantity of books in the transaction
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrnQuantitiesId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The transaction ID
     */
    private Long transaction;

    /**
     * The books involved in the transaction
     */
    private Long book;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        TrnQuantitiesId that = (TrnQuantitiesId) o;

        if (this.transaction != null ? !this.transaction.equals(that.transaction) : that.transaction != null) {
            return false;
        }

        return this.book != null ? this.book.equals(that.book) : that.book == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = this.transaction != null ? this.transaction.hashCode() : 0;
        result = 31 * result + (this.book != null ? this.book.hashCode() : 0);
        return result;
    }
}
