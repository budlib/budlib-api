package com.budlib.api.model;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_id")
    private int classCodeId;

    /**
     * Class code or class name
     */
    @Column(name = "class_code")
    private String classCode;

    @ManyToMany(mappedBy = "classCode")
    private List<Loaner> facultyList;
}
