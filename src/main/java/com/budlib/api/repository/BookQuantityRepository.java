package com.budlib.api.repository;

import com.budlib.api.model.BookQuantity;
// import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookQuantityRepository extends JpaRepository<BookQuantity, Long> {

}
