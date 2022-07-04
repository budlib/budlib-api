package com.budlib.api.repository;

import com.budlib.api.model.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on Book
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
