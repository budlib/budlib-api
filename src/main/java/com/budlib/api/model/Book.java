package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

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
    private String librarySection;

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
    @OneToMany(mappedBy = "bookId", cascade = { CascadeType.ALL })
    @JsonIgnore
    private List<TrnQuantities> trnQuantities;

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
    @JsonIgnore
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
     * Returns the tags of the book
     *
     * @return space separated list of tags
     */
    public String getBookTags() {
        StringBuilder sb = new StringBuilder();

        for (Tag t : this.tags) {
            sb.append(" " + t.getTagName());
        }

        return sb.toString().trim();
    }
}
