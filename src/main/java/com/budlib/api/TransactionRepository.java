package com.budlib.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, Long> {
    List<Transaction> findBystudentLId(Long studentLId);

    List<Transaction> findBybookId(Long bookId);

}
