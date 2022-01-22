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

    @GetMapping(value = "/books/title/{title}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Book> getProductByTitle(@PathVariable String title) {
        Book p = bookRepository.findByTitle(title).get(0);
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/books/isbn/{isbn}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Book> getProductByISBN(@PathVariable String isbn) {
        Book p = bookRepository.findByISBN(isbn).get(0);
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/books/author/{authors}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Book>> getProductByAuthor(@PathVariable String authors) {
        Collection<Book> books = bookRepository.findByAuthors(authors).stream()
                .collect(Collectors.toList());
        System.out.println("Number of books: " + books.size());
        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "/books/publisher/{publisher}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Book>> getProductByPublisher(@PathVariable String publisher) {
        Collection<Book> books = bookRepository.findByPublisher(publisher).stream()
                .collect(Collectors.toList());
        System.out.println("Number of books: " + books.size());
        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "/books/tag/{tag}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Book>> getProductByTag(@PathVariable String tag) {
        Collection<Book> books = bookRepository.findByTagsIn(tag).stream()
                .collect(Collectors.toList());
        System.out.println("Number of books: " + books.size());
        return ResponseEntity.ok().body(books);
    }

    @GetMapping(value = "/books", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Book>> getBooks() {
        Collection<Book> books = bookRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of books: " + books.size());
        return ResponseEntity.ok().body(books);
    }

    @PostMapping(value = "/addbooks", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<?> insertProduct(@RequestBody Book book) {

        book.setId(bookRepository.count() + 1);
        bookRepository.save(book);
        URI uri = URI.create("/bookrepo/books/" + book.getId());
        return ResponseEntity.created(uri).body(book);

    }

    @PutMapping(value = "/updatebooks/{id}", consumes = { "application/json", "application/xml" })
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
