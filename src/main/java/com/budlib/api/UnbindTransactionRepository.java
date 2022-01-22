package com.budlib.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnbindTransactionRepository extends MongoRepository<UnbindTransaction, Long> {
    List<UnbindTransaction> findByLoaner(String loaner);

    List<UnbindTransaction> findBybookId(Long bookId);

}
