package com.budlib.api.repository;

import com.budlib.api.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on Loan
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

}
