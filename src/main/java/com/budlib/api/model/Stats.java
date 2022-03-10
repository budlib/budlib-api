package com.budlib.api.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;

/**
 * Represents stats for the library dashboard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stats")
public class Stats implements Serializable {
    /**
     * Total number of unique titles in the library
     */
    private long uniqueTitles;

    /**
     * Sum of all copies of all the titles in the library
     */
    private long totalCopies;

    /**
     * Count of all loaners in the system
     */
    private long totalLoaners;

    /**
     * Sum of all outstanding loans
     */
    private long totalOutstandingCopies;
}
