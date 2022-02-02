package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;

/**
 * Represents the tags of books
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tag")
public class Tag implements Serializable {
    /**
     * Internal unique ID of the tag
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private long tagId;

    /**
     * String representation of the tag
     */
    @Column(name = "tag_name")
    private String tagName;

    /**
     * Books that has this tag
     */
    @ManyToMany(mappedBy = "tags")
    private List<Book> books;
}
