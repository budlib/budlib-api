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
     * What all branches have this book
     */
    @OneToMany(mappedBy = "bookId", cascade = { CascadeType.ALL })
    @JsonIgnore
    private List<BookQuantity> availableBranch;

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
    @JoinTable(name = "book_tag", joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id", foreignKey = @ForeignKey(name = "fk_booktag_bookid")), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id", foreignKey = @ForeignKey(name = "fk_booktag_tagid")))
    @JsonIgnore
    private List<Tag> tags;

    /**
     * URL of thumbnail the book
     */
    @Column(name = "imagelink")
    private String imageLink;

    /**
     * Language of the book
     */
    @Column(name = "language")
    private String language;

    /**
     * Retail price of the book
     */
    @Column(name = "retailprice")
    private Double retailPrice;

    /**
     * Price of the book at which it is/was usually procured in the library
     */
    @Column(name = "libraryprice")
    private Double internalLibraryPrice;

    /**
     * Online buying link on amazon.ca
     * TODO: check if its datatype can be changed
     */
    @Column(name = "amazonlink")
    private String amazonCaLink;

    /**
     * Computes total quantities of available books across all branches
     *
     * @return total quantities of the available books across all branches
     */
    public long getQuantities() {
        long count = 0;

        for (BookQuantity i : this.availableBranch) {
            count += i.getAvailable();
        }

        return count;
    }

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
