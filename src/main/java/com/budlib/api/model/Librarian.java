package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Represents the librarian
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "librarian")
public class Librarian implements Serializable {
    /**
     * Internal unique ID of the librarian
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "librarian_id")
    private long librarianId;

    /**
     * Login username of the librarian
     */
    @Column(name = "username")
    private String userName;

    /**
     * First name of the librarian
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Middle name of the librarian
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Last name of the librarian
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Email of the librarian
     */
    @Column(name = "email")
    private String email;

    /**
     * Login password of the librarian
     */
    @Column(name = "password")
    private String password;

    /**
     * Role in the library
     */
    @Column(name = "role")
    private String role;

    /**
     * Coordination history of the Librarian
     */
    @OneToMany(mappedBy = "librarian")
    @JsonIgnore
    private List<Transaction> transactionHistory;

    /**
     * Returns the full name of the librarian
     *
     * @return full name of the librarian
     */
    @JsonProperty("fullName")
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
