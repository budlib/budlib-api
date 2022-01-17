package com.budlib.api;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LibrarianRepository extends MongoRepository<Librarian, Long> {
    List<Librarian> findByuserName(String username);

}
