package com.budlib.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;
import com.budlib.api.model.Book;
import com.budlib.api.model.UnbindTransaction;
import com.budlib.api.repository.BookRepository;
import com.budlib.api.repository.UnbindTransactionRepository;
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
@RequestMapping("/utransactionrepo")
@CrossOrigin
public class UnbindTransactionController {

    @Autowired
    private UnbindTransactionRepository unbindTransactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping(value = "/transactions/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<UnbindTransaction> getTransactionById(@PathVariable long id) {
        UnbindTransaction p = unbindTransactionRepository.findById(id).get();
        if (p == null)
            return ResponseEntity.notFound().build();

        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/transactions", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<UnbindTransaction>> getBooks() {
        Collection<UnbindTransaction> transactions = unbindTransactionRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of Transactions: " + transactions.size());
        return ResponseEntity.ok().body(transactions);
    }

    @PostMapping(value = "/transactions", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<UnbindTransaction> insertProduct(@RequestBody UnbindTransaction transaction) {
        System.out.println("Book ID: " + transaction.getBookId());
        System.out.println("Exists in books: " + bookRepository.existsById(transaction.getBookId()));
        if (bookRepository.existsById(transaction.getBookId()) == true) {
            transaction.setId(unbindTransactionRepository.count() + 1);

            Book book = bookRepository.findById(transaction.getBookId()).get();
            if (transaction.getTransactionType().startsWith("B")) {
                if (book.getAvailable() > 0) {
                    book.setAvailable(book.getAvailable() - 1);
                    bookRepository.save(book);
                }

                else {
                    System.out.println("isloaned out should be false but is true");
                    return ResponseEntity.notFound().build();
                }
            }

            else {
                if (book.getAvailable() < book.getQty()) {
                    book.setAvailable(book.getAvailable() + 1);
                    bookRepository.save(book);
                }

                else {
                    System.out.println("isloaned out should be true but is false");
                    return ResponseEntity.notFound().build();
                }
            }

            unbindTransactionRepository.save(transaction);
            URI uri = URI.create("/utransactionrepo/transactions/" + transaction.getId());

            return ResponseEntity.created(uri).body(transaction);
        }

        else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping(value = "/transactions/{id}", consumes = { "application/json", "application/xml" })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @RequestBody UnbindTransaction transaction) {
        if (!unbindTransactionRepository.existsById(transaction.getId()))
            return ResponseEntity.notFound().build();

        else
            unbindTransactionRepository.save(transaction);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        UnbindTransaction transaction = unbindTransactionRepository.findById(id).get();
        if (transaction == null)
            return ResponseEntity.notFound().build();

        else {
            unbindTransactionRepository.delete(transaction);
            return ResponseEntity.ok().build();
        }
    }
}
