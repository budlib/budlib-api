package com.budlib.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByAuthors(String authors);

    List<Book> findByISBN(String ISBN);

    List<Book> findByPublisher(String publisher);

    List<Book> findByTagsIn(String tag);

}
