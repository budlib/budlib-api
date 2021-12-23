package com.budlib.api;

import java.net.URI;
import java.util.Collection;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookrepo")
@CrossOrigin
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping(value = "/books/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Book> getProductById(@PathVariable long id) {
        Book p = bookRepository.findById(id).get();
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/books", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Book>> getBooks() {
        Collection<Book> books = bookRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of books: " + books.size());
        return ResponseEntity.ok().body(books);
    }

    @PostMapping(value = "/books", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<Book> insertProduct(@RequestBody Book book) {
        bookRepository.insert(book);
        URI uri = URI.create("/bookrepo/books/" + book.getId());
        return ResponseEntity.created(uri).body(book);
    }

    @PutMapping(value = "/books/{id}", consumes = { "application/json", "application/xml" })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @RequestBody Book book) {
        if (!bookRepository.existsById(book.getId()))
            return ResponseEntity.notFound().build();
        else
            bookRepository.save(book);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        Book book = bookRepository.findById(id).get();
        if (book == null)
            return ResponseEntity.notFound().build();
        else {
            bookRepository.delete(book);
            return ResponseEntity.ok().build();
        }

    }

}
