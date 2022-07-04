package com.budlib.api.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the tags of books
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @JsonIgnore
    private List<Book> books;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Tag [id=%d, name=\"%s\"]", this.tagId, this.tagName);
    }
}
