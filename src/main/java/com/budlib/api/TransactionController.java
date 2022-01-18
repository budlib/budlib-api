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
@RequestMapping("/transactionrepo")
@CrossOrigin
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping(value = "/transactions/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        Transaction p = transactionRepository.findById(id).get();
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/transactions", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Transaction>> getBooks() {
        Collection<Transaction> transactions = transactionRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of Transactions: " + transactions.size());
        return ResponseEntity.ok().body(transactions);
    }

    @PostMapping(value = "/transactions", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<Transaction> insertProduct(@RequestBody Transaction transaction) {
        if (studentRepository.existsById(transaction.getStudentLId()) == true
                && bookRepository.existsById(transaction.getBookId()) == true) {
            transaction.setId(transactionRepository.count() + 1);

            Book book = bookRepository.findById(transaction.getBookId()).get();
            if (transaction.getTransactionType().startsWith("B")) {
                if (book.isLoaned_out() == false) {
                    book.setLoaned_out(true);
                    bookRepository.save(book);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                if (book.isLoaned_out() == true) {
                    book.setLoaned_out(false);
                    bookRepository.save(book);
                } else {
                    return ResponseEntity.notFound().build();
                }

            }

            transactionRepository.save(transaction);
            URI uri = URI.create("/transactionrepo/transactions/" + transaction.getId());
            return ResponseEntity.created(uri).body(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping(value = "/transactions/{id}", consumes = { "application/json", "application/xml" })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @RequestBody Transaction transaction) {
        if (!transactionRepository.existsById(transaction.getId()))
            return ResponseEntity.notFound().build();
        else
            transactionRepository.save(transaction);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        Transaction transaction = transactionRepository.findById(id).get();
        if (transaction == null)
            return ResponseEntity.notFound().build();
        else {
            transactionRepository.delete(transaction);
            return ResponseEntity.ok().build();
        }

    }

}