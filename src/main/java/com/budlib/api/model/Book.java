package com.budlib.api.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.budlib.api.enums.LibrarySection;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a book in the library
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Internal unique ID of the book
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private long bookId;

    /**
     * Title of the book
     */
    @Column(name = "title")
    private String title;

    /**
     * Subtitle of the book
     */
    @Column(name = "subtitle")
    private String subtitle;

    /**
     * Authors of the book
     */
    @Column(name = "author")
    private String authors;

    /**
     * Publishing company of the book
     */
    @Column(name = "publisher")
    private String publisher;

    /**
     * Edition number of the book
     */
    @Column(name = "edition")
    private String edition;

    /**
     * Year of publishing
     */
    @Column(name = "year")
    private String year;

    /**
     * Language of the book
     */
    @Column(name = "language")
    private String language;

    /**
     * ISBN 10 of the book
     */
    @Column(name = "isbn_10")
    private String isbn_10;

    /**
     * ISBN 13 of the book
     */
    @Column(name = "isbn_13")
    private String isbn_13;

    /**
     * Section in the library branch where the book is usually placed
     */
    @Column(name = "library_section")
    private LibrarySection librarySection;

    /**
     * Total quantity at the library branch
     */
    @Column(name = "total_quantity")
    private int totalQuantity;

    /**
     * Available quantity at the library branch
     */
    @Column(name = "available_quantity")
    private int availableQuantity;

    /**
     * Copies of the books exchanging hands
     */
    @OneToMany(mappedBy = "book", cascade = { CascadeType.ALL })
    @JsonIgnore
    private List<TrnQuantities> trnQuantities;

    /**
     * List of current loaners
     */
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Loan> currentLoans;

    /**
     * Any particular notes related to the book goes here
     */
    @Column(name = "notes")
    private String notes;

    /**
     * Tags related to the book
     */
    @ManyToMany
    @JoinTable(name = "book_tag", joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id", foreignKey = @ForeignKey(name = "fk_booktag_book")), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id", foreignKey = @ForeignKey(name = "fk_booktag_tag")))
    private List<Tag> tags;

    /**
     * URL of thumbnail the book
     */
    @Column(name = "image_link")
    private String imageLink;

    /**
     * Retail price of the book
     */
    @Column(name = "retail_price")
    private Double priceRetail;

    /**
     * Price of the book at which it is/was usually procured in the library
     */
    @Column(name = "library_price")
    private Double priceLibrary;

    /**
     * Setter for ISBN 10 by removing special characters like dashes/hyphens
     *
     * @param isbn_10 ISBN 10 of the book
     */
    public void setIsbn_10(String isbn_10) {
        if (isbn_10 != null) {
            this.isbn_10 = isbn_10.toUpperCase().replaceAll("[^a-zA-Z0-9]", "");
        }

        else {
            this.isbn_10 = isbn_10;
        }
    }

    /**
     * Setter for ISBN 13 by removing special characters like dashes/hyphens
     *
     * @param isbn_13 ISBN 13 of the book
     */
    public void setIsbn_13(String isbn_13) {
        if (isbn_13 != null) {
            this.isbn_13 = isbn_13.toUpperCase().replaceAll("[^a-zA-Z0-9]", "");
        }

        else {
            this.isbn_13 = isbn_13;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format("Book [bookId=%d, title=\"%s\", subtitle=\"%s\", authors=\"%s\", publisher=\"%s\", edition=\"%s\","
                + "year=\"%s\", language=\"%s\", isbn_10=\"%s\", isbn_13=\"%s\", librarySection=\"%s\", totalQty=%d, "
                + "availableQty=%d, transactionQtys=%s, currentLoans=%s, notes=\"%s\", tags=%s, imageLink=\"%s\", "
                + "priceRetail=%s, priceLibrary=%s]",
                this.bookId,
                this.title,
                this.subtitle,
                this.authors,
                this.publisher,
                this.edition,
                this.year,
                this.language,
                this.isbn_10,
                this.isbn_13,
                this.librarySection,
                this.totalQuantity,
                this.availableQuantity,
                this.trnQuantitiesToString(),
                this.currentLoansToString(),
                this.notes,
                this.tagListToString(),
                this.imageLink,
                this.priceRetail,
                this.priceLibrary
                );
    }

    /**
     * Converts the list of tags to a string
     *
     * @return String of tags
     */
    private String tagListToString() {
        if (this.tags != null) {
            StringBuilder tagString = new StringBuilder();

            for (int i = 0; i < this.tags.size(); i++) {
                tagString.append(this.tags.get(i).toString());

                if (i != this.tags.size() - 1) {
                    tagString.append(", ");
                }
            }

            return tagString.toString();
        }

        return "null";
    }

    /**
     * Converts the list of transaction quantities to a string
     *
     * @return String of transaction quantities
     */
    private String trnQuantitiesToString() {
        if (this.trnQuantities != null) {
            StringBuilder trnQuantitiesString = new StringBuilder();

            for (int i = 0; i < this.trnQuantities.size(); i++) {
                trnQuantitiesString.append(this.trnQuantities.get(i).toString());
            }

            return trnQuantitiesString.toString();
        }

        return "null";
    }

    /**
     * Converts the list of currentLoans to a string
     *
     * @return String of currentLoans
     */
    private String currentLoansToString() {
        if (this.currentLoans != null) {
            StringBuilder currentLoansString = new StringBuilder();

            for (int i = 0; i < this.currentLoans.size(); i++) {
                currentLoansString.append(this.currentLoans.get(i).toString());
            }

            return currentLoansString.toString();
        }

        return "null";
    }
}
