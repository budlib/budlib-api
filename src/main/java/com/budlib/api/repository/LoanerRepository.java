package com.budlib.api.repository;

import com.budlib.api.model.Loaner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on Loaner
 */
@Repository
public interface LoanerRepository extends JpaRepository<Loaner, Long> {

}
