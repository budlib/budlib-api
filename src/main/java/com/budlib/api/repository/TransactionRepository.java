package com.budlib.api.repository;

import com.budlib.api.model.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on Transaction
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
