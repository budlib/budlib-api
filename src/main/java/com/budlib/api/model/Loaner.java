package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import lombok.*;
import com.fasterxml.jackson.annotation.*;

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

    /**
     * If this loaner is a student or a faculty
     */
    @JsonProperty("isStudent")
    @Column(name = "is_student")
    private boolean isStudent;

    /**
     * For faculty Mr, Mrs, Ms, Master, Miss, Dr, Prof, etc
     */
    @Column(name = "salutation")
    private String salutation;

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
     * Borrowing history of the loaner
     */
    @OneToMany(mappedBy = "loaner")
    @JsonIgnore
    private List<Transaction> transactionHistory;

    /**
     * Current loans by the loaner
     */
    @OneToMany(mappedBy = "loaner")
    @JsonIgnore
    private List<Loan> currentLoans;

    /**
     * Returns the full name of the loaner
     *
     * @return full name of the loaner
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

    /**
     * Returns the full name of the loaner with salutation
     *
     * @return full name of the loaner
     */
    @JsonProperty("fullNameWithSalutation")
    public String getFullNameWithSalutation() {
        if (this.salutation == null || this.salutation.equals("")) {
            return this.getFullName();
        }

        else {
            return this.salutation + " " + this.getFullName();
        }
    }

    /**
     * Returns the copies of returningBook that loaner has
     *
     * @param returningBook the book that loaner still holds
     * @return number of copies
     */
    public int findOutstandingCopiesByBook(Book returningBook) {
        for (Loan eachLoan : this.currentLoans) {
            if (eachLoan.getBook().getBookId() == returningBook.getBookId()) {
                return eachLoan.getCopies();
            }
        }

        return 0;
    }

    /**
     * Update the loans by the user after they return a book
     *
     * @param returningBook  the book loaner returned
     * @param copiesReturned the copies of the book they returned
     * @return loans returned by the Loaner
     */
    public List<Loan> updateLoans(Book returningBook, int copiesReturned) {
        List<Loan> updatedLoans = new ArrayList<>();
        List<Loan> settledLoans = new ArrayList<>();

        for (Loan eachLoan : this.currentLoans) {
            if (returningBook.getBookId() == eachLoan.getBook().getBookId()) {
                int copiesStillLoaned = eachLoan.getCopies() - copiesReturned;

                if (copiesStillLoaned <= 0) {
                    settledLoans.add(eachLoan);
                    continue;
                }

                else {
                    eachLoan.setCopies(copiesStillLoaned);
                    updatedLoans.add(eachLoan);
                }
            }

            else {
                updatedLoans.add(eachLoan);
            }
        }

        this.currentLoans = updatedLoans;
        return settledLoans;
    }
}
