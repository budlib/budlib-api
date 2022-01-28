package com.budlib.api.controller;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;
import com.budlib.api.model.Librarian;
import com.budlib.api.repository.LibrarianRepository;
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
@RequestMapping("/librarianrepo")
@CrossOrigin
public class LibrarianController {
    @Autowired
    LibrarianRepository librarianRepository;

    @GetMapping(value = "/librarians/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Librarian> getStudentById(@PathVariable long id) {
        Librarian p = librarianRepository.findById(id).get();
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/librarians/user/{username}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Librarian> getLibByusername(@PathVariable String username) {
        Librarian p = librarianRepository.findByuserName(username).get(0);
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/librarians", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Librarian>> getBooks() {
        Collection<Librarian> librarian = librarianRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of Librarians: " + librarian.size());
        return ResponseEntity.ok().body(librarian);
    }

    @PostMapping(value = "/addlibrarians", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<?> insertProduct(@RequestBody Librarian librarian) {
        if (librarianRepository.findByuserName(librarian.getUserName()).size() > 0) {
            return (ResponseEntity<?>) ResponseEntity.status(409);
        }
        librarian.setId(librarianRepository.count() + 1);
        librarianRepository.save(librarian);
        URI uri = URI.create("/librarianrepo/librarians/" + librarian.getId());
        return ResponseEntity.created(uri).body(librarian);
    }

    @PutMapping(value = "/updatelibrarians/{id}", consumes = { "application/json", "application/xml" })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @RequestBody Librarian librarian) {
        if (!librarianRepository.existsById(id))
            return ResponseEntity.notFound().build();
        else {
            librarian.setUserName(librarianRepository.findById(id).get().getUserName());
            librarianRepository.save(librarian);
            return ResponseEntity.ok().build();

        }

    }

    @DeleteMapping("/librarians/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        Librarian librarian = librarianRepository.findById(id).get();
        if (librarian == null)
            return ResponseEntity.notFound().build();
        else {
            librarianRepository.delete(librarian);
            return ResponseEntity.ok().build();
        }

    }
}
