package com.budlib.api.response;

import lombok.*;

/**
 * Represents stats for the library dashboard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
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
