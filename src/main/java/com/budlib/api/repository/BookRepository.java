package com.budlib.api.repository;

import com.budlib.api.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookRepository extends MongoRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByAuthors(String authors);

    List<Book> findByPublisher(String publisher);

    List<Book> findByIsbn(String isbn);

    List<Book> findByTagsIn(String tag);
}
