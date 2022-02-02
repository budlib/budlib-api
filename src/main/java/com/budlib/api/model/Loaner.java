package com.budlib.api.model;

import javax.persistence.*;
import java.util.List;
import java.io.Serializable;
import lombok.*;

/**
 * Represents a loaner in the system
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loaner")
public class Loaner implements Serializable {
    /**
     * Unique ID of the loaner
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loaner_id")
    private long loanerId;

    /**
     * Employee ID for Faculty and Registration ID for students
     */
    @Column(name = "school_id")
    private String schoolId;

    @Column(name = "is_student")
    private boolean isStudent;

    /**
     * First name of the loaner
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Middle name of the loaner
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Last name of the loaner
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Name of the student's mother
     */
    @Column(name = "mother_name")
    private String motherName;

    /**
     * Name of the student's father
     */
    @Column(name = "father_name")
    private String fatherName;

    /**
     * For faculty Mr, Mrs, Ms, Master, Miss, Dr, Prof, etc
     */
    @Column(name = "salutation")
    private String salutation;

    /**
     * Classes the faculty is handling
     */
    @ManyToMany
    @JoinTable(name = "faculty_class", joinColumns = @JoinColumn(name = "loaner_id", referencedColumnName = "loaner_id", foreignKey = @ForeignKey(name = "fk_faculty_employeeid")), inverseJoinColumns = @JoinColumn(name = "class_id", referencedColumnName = "class_id", foreignKey = @ForeignKey(name = "fk_class_classid")))
    private List<ClassCode> classCode;

    /**
     * Returns the full name of the loaner
     *
     * @return full name of the loaner
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();

        if (this.firstName != null) {
            sb.append(this.firstName);
        }

        if (this.middleName != null) {
            sb.append(" " + this.middleName);
        }

        if (this.lastName != null) {
            sb.append(" " + this.lastName);
        }

        // to handle null if no name is provided
        if (sb.length() == 0) {
            return "";
        }

        else {
            return sb.toString();
        }
    }
}
