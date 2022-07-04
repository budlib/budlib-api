package com.budlib.api.repository;

import com.budlib.api.model.Librarian;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for CRUD on Librarian
 */
@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {

}
