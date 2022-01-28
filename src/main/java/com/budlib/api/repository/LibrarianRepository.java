package com.budlib.api.repository;

import java.util.List;
import com.budlib.api.model.Librarian;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LibrarianRepository extends MongoRepository<Librarian, Long> {
    List<Librarian> findByuserName(String username);
}
