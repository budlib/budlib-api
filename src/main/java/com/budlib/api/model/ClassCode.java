package com.budlib.api.model;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Represents a class in the school
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class_code")
public class ClassCode implements Serializable {
    /**
     * Unique ID of the class
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private long classCodeId;

    /**
     * Class code or class name
     */
    @Column(name = "class_code")
    private String classCode;

    @ManyToMany(mappedBy = "classCode")
    @JsonBackReference
    private List<Loaner> facultyList;
}
