package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;

/**
 * Represents the library coordinator
 */
@Entity
@Getter
@Setter
@Table(name = "librarian")
public class Librarian implements Serializable {
    /**
     * Internal unique ID of the library coordinator
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "librarian_id")
    private Long librarianId;

    /**
     * First name of the coordinator
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Middle name of the coordinator
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Last name of the coordinator
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Login username of the coordinator
     */
    @Column(name = "username")
    private String userName;

    /**
     * Email of the coordinator
     */
    @Column(name = "email")
    private String email;

    /**
     * Login password of the coordinator
     * TODO: replace with encrypted string
     */
    @Column(name = "password")
    private String password;

    /**
     * Role in the library
     */
    @Column(name = "role")
    private String role;

    // TODO: add branch wise coordinator and a superadmin
}
