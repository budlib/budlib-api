package com.budlib.api.repository;

import java.util.List;
import com.budlib.api.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, Long> {
    List<Transaction> findBystudentLId(Long studentLId);

    List<Transaction> findBybookId(Long bookId);
}
