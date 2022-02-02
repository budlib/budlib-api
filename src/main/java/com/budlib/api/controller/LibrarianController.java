package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/librarian")
public class LibrarianController {
    @Autowired
    LibrarianRepository librarianRepository;

    /**
     * Search the librarian by id
     *
     * @param id librarian id
     * @return list of librarians with id; the list would have utmost one element
     */
    private List<Librarian> searchLibrarianById(Long id) {
        Optional<Librarian> librarianOptional = this.librarianRepository.findById(id);

        if (librarianOptional.isPresent()) {
            List<Librarian> searchResults = new ArrayList<>();
            searchResults.add(librarianOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search the librarians by their email
     *
     * @param alllibrarians list of all librarians
     * @param sT            search term
     * @return filtered list of librarian with email meeting the search term
     */
    private List<Librarian> searchLibrarianByEmail(List<Librarian> allLibrarians, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Librarian> searchResults = new ArrayList<>();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getEmail() != null && eachLibrarian.getEmail().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachLibrarian);
            }
        }

        return searchResults;
    }

    /**
     * Endpoint for GET - fetch librarian by id
     *
     * @param id librarian id
     * @return librarian
     */
    @GetMapping(path = "{librarianId}")
    public ResponseEntity<?> getLibrarianById(@PathVariable("librarianId") Long id) {
        List<Librarian> l = this.searchLibrarianById(id);

        if (l == null) {
            String message = "Librarian not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(l.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all librarians meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of librarians meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchLibrarian(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<Librarian> allLibrarians = this.librarianRepository.findAll();

        if (searchBy == null || searchTerm == null) {
            return ResponseEntity.status(HttpStatus.OK).body(allLibrarians);
        }

        else if (searchBy.equals("") || searchTerm.equals("")) {
            return ResponseEntity.status(HttpStatus.OK).body(allLibrarians);
        }

        else if (searchBy.equalsIgnoreCase("id")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLibrarianById(Long.valueOf(searchTerm)));
        }

        else if (searchBy.equalsIgnoreCase("email")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLibrarianByEmail(allLibrarians, searchTerm));
        }

        else {
            String message = "Invalid librarian search operation";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Endpoint for POST - save the librarian in db
     *
     * @param l librarian details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addlibrarian(@RequestBody Librarian l) {
        // reset the id to 0 to prevent overwrite
        l.setLibrarianId(0L);

        this.librarianRepository.save(l);

        String message = "Librarian added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for PUT - update librarian in db
     *
     * @param l           the updated librarian details in json
     * @param librarianId the id of the librarian to be updated
     * @return the message
     */
    @PutMapping(path = "{librarianId}")
    public ResponseEntity<?> updatelibrarian(@RequestBody Librarian l, @PathVariable("librarianId") Long librarianId) {
        Optional<Librarian> librarianOptional = this.librarianRepository.findById(librarianId);

        if (librarianOptional.isPresent()) {
            l.setLibrarianId(librarianId);
            this.librarianRepository.save(l);

            String message = "Librarian updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        else {
            String message = "Librarian not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }
    }

    /**
     * Endpoint for DELETE - delete librarian from db
     *
     * @param librarianId librarian id
     */
    @DeleteMapping(path = "{librarianId}")
    public ResponseEntity<?> deletelibrarian(@PathVariable("librarianId") Long librarianId) {
        if (!this.librarianRepository.existsById(librarianId)) {
            String message = "Librarian not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            this.librarianRepository.deleteById(librarianId);
            String message = "Librarian deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
