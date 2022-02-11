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

/**
 * Controller for loaner
 */
@CrossOrigin
@RestController
@RequestMapping("api/loaners")
public class LoanerController {
    @Autowired
    private LoanerRepository loanerRepository;

    /**
     * Search the loaner by id
     *
     * @param id loaner id
     * @return list of loaners with id; the list would have utmost one element
     */
    private List<Loaner> searchLoanerById(Long id) {
        Optional<Loaner> loanerOptional = this.loanerRepository.findById(id);

        if (loanerOptional.isPresent()) {
            List<Loaner> searchResults = new ArrayList<>();
            searchResults.add(loanerOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search the loaners by their school id
     *
     * @param allloaners list of all loaners
     * @param sT         search term
     * @return filtered list of loaner with name meeting the search term
     */
    private List<Loaner> searchLoanerBySchoolId(List<Loaner> allLoaners, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Loaner> searchResults = new ArrayList<>();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachLoaner);
            }
        }

        return searchResults;
    }

    /**
     * Search the loaners by their name
     *
     * @param allloaners list of all loaners
     * @param sT         search term
     * @return filtered list of loaner with name meeting the search term
     */
    private List<Loaner> searchLoanerByName(List<Loaner> allLoaners, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Loaner> searchResults = new ArrayList<>();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getFullName() != null && eachLoaner.getFullName().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachLoaner);
            }
        }

        return searchResults;
    }

    /**
     * Search the loaner by their parent's name
     *
     * @param allLoaners list of all loaners
     * @param sT         search term
     * @return filtered list of students with parent's name meeting the search term
     */
    private List<Loaner> searchStudentByParentName(List<Loaner> allLoaners, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Loaner> searchResults = new ArrayList<>();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getMotherName() != null && eachLoaner.getFullName().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachLoaner);
            }

            else if (eachLoaner.getFatherName() != null
                    && eachLoaner.getFullName().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachLoaner);
            }
        }

        return searchResults;
    }

    /**
     * Endpoint for GET - fetch loaner by id
     *
     * @param id loaner id
     * @return loaner
     */
    @GetMapping(path = "{loanerId}")
    public ResponseEntity<?> getLoanerById(@PathVariable("loanerId") Long id) {
        List<Loaner> s = this.searchLoanerById(id);

        if (s == null) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(s.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all loaners meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of loaners meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchLoaner(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        if (searchBy == null || searchTerm == null) {
            return ResponseEntity.status(HttpStatus.OK).body(allLoaners);
        }

        else if (searchBy.equals("") || searchTerm.equals("")) {
            return ResponseEntity.status(HttpStatus.OK).body(allLoaners);
        }

        else if (searchBy.equalsIgnoreCase("id")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLoanerById(Long.valueOf(searchTerm)));
        }

        else if (searchBy.equalsIgnoreCase("schoolId")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLoanerBySchoolId(allLoaners, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("name")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLoanerByName(allLoaners, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("parentName")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchStudentByParentName(allLoaners, searchTerm));
        }

        else {
            String message = "Invalid loaner search operation";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Check if the loaner exists in the system. Works only if Loaner.schoolId is
     * not null or empty
     *
     * @param l Loaner to be added or updated
     * @return true if unique, false otherwise
     */
    private boolean checkLoanerUniqueness(Loaner l) {
        if (l.getSchoolId() == null || l.getSchoolId().equals("")) {
            return true;
        }

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getLoanerId() == l.getLoanerId()) {
                continue;
            }

            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().equalsIgnoreCase(l.getSchoolId()))
                return false;
        }

        return true;
    }

    /**
     * Endpoint for POST - save the loaner in db
     *
     * @param l loaner details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addLoaner(@RequestBody Loaner l) {
        // reset the id to 0 to prevent overwrite
        l.setLoanerId(0L);

        if (this.checkLoanerUniqueness(l)) {
            this.loanerRepository.save(l);
            String message = "Loaner added successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        else {
            String message = "Loaner already exists";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Endpoint for PUT - update loaner in db
     *
     * @param l        the updated loaner details in json
     * @param loanerId the id of the loaner to be updated
     * @return the message
     */
    @PutMapping(path = "{loanerId}")
    public ResponseEntity<?> updateLoaner(@RequestBody Loaner l, @PathVariable("loanerId") Long loanerId) {
        Optional<Loaner> loanerOptional = this.loanerRepository.findById(loanerId);

        if (loanerOptional.isPresent()) {
            l.setLoanerId(loanerId);

            if (this.checkLoanerUniqueness(l)) {
                this.loanerRepository.save(l);
                String message = "Loaner updated successfully";
                return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
            }

            else {
                String message = "Loaner already exists";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }
        }

        else {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }
    }

    /**
     * Endpoint for DELETE - delete loaner from db
     *
     * @param loanerId loaner id
     */
    @DeleteMapping(path = "{loanerId}")
    public ResponseEntity<?> deleteLoaner(@PathVariable("loanerId") Long loanerId) {
        if (!this.loanerRepository.existsById(loanerId)) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            this.loanerRepository.deleteById(loanerId);
            String message = "Loaner deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
