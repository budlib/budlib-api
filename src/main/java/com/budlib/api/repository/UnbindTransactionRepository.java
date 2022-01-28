package com.budlib.api.repository;

import java.util.List;
import com.budlib.api.model.UnbindTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnbindTransactionRepository extends MongoRepository<UnbindTransaction, Long> {
    List<UnbindTransaction> findByLoaner(String loaner);

    List<UnbindTransaction> findBybookId(Long bookId);
}
