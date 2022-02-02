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
@RequestMapping("api/branch")
public class LibraryBranchController {
    @Autowired
    private LibraryBranchRepository libraryBranchRepository;

    /**
     * Search the branch by id
     *
     * @param id branch id
     * @return list of branches with id; the list would have utmost one element
     */
    private List<LibraryBranch> searchLibraryBranchById(Long id) {
        Optional<LibraryBranch> branchOptional = this.libraryBranchRepository.findById(id);

        if (branchOptional.isPresent()) {
            List<LibraryBranch> searchResults = new ArrayList<>();
            searchResults.add(branchOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search the branches by their name
     *
     * @param allBranches list of all branches
     * @param sT          search term
     * @return filtered list of branches with name meeting the search term
     */
    private List<LibraryBranch> searchLibraryBranchByName(List<LibraryBranch> allBranches, String sT) {
        String searchTerm = sT.toLowerCase();
        List<LibraryBranch> searchResults = new ArrayList<>();

        for (LibraryBranch eachBranch : allBranches) {
            if (eachBranch.getBranchName() != null && eachBranch.getBranchName().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBranch);
            }
        }

        return searchResults;
    }

    /**
     * Search the branches by their address
     *
     * @param allBranches list of all branches
     * @param sT          search term
     * @return filtered list of branches with address meeting the search term
     */
    private List<LibraryBranch> searchLibraryBranchByAddress(List<LibraryBranch> allBranches, String sT) {
        String searchTerm = sT.toLowerCase();
        List<LibraryBranch> searchResults = new ArrayList<>();

        for (LibraryBranch eachBranch : allBranches) {
            if (eachBranch.getBranchAddress() != null
                    && eachBranch.getBranchAddress().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachBranch);
            }
        }

        return searchResults;
    }

    /**
     * Endpoint for GET - fetch branch by id
     *
     * @param id branch id
     * @return branch
     */
    @GetMapping(path = "{branchId}")
    public ResponseEntity<?> getLibraryBranchById(@PathVariable("branchId") Long id) {
        List<LibraryBranch> s = this.searchLibraryBranchById(id);

        if (s == null) {
            String message = "Branch not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(s.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all branches meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of branches meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchLibraryBranches(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<LibraryBranch> allBranches = this.libraryBranchRepository.findAll();

        if (searchBy == null || searchTerm == null) {
            return ResponseEntity.status(HttpStatus.OK).body(allBranches);
        }

        else if (searchBy.equals("") || searchTerm.equals("")) {
            return ResponseEntity.status(HttpStatus.OK).body(allBranches);
        }

        else if (searchBy.equalsIgnoreCase("id")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLibraryBranchById(Long.valueOf(searchTerm)));
        }

        else if (searchBy.equalsIgnoreCase("name")) {
            return ResponseEntity.status(HttpStatus.OK).body(this.searchLibraryBranchByName(allBranches, searchTerm));
        }

        else if (searchBy.equalsIgnoreCase("address")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.searchLibraryBranchByAddress(allBranches, searchTerm));
        }

        else {
            String message = "Invalid branch search operation";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * Endpoint for POST - save the branch in db
     *
     * @param b branch details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addBranch(@RequestBody LibraryBranch b) {
        // reset the id to 0 to prevent overwrite
        b.setBranchId(0L);

        this.libraryBranchRepository.save(b);

        String message = "Branch added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for PUT - update branch in db
     *
     * @param b        the updated branch details in json
     * @param branchId the id of the branch to be updated
     * @return the message
     */
    @PutMapping(path = "{branchId}")
    public ResponseEntity<?> updateLibraryBranch(@RequestBody LibraryBranch b,
            @PathVariable("branchId") Long branchId) {
        Optional<LibraryBranch> branchOptional = this.libraryBranchRepository.findById(branchId);

        if (branchOptional.isPresent()) {
            b.setBranchId(branchId);
            this.libraryBranchRepository.save(b);

            String message = "Branch updated successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }

        else {
            String message = "Branch not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }
    }

    /**
     * Endpoint for DELETE - delete branch from db
     *
     * @param branchId branch id
     */
    @DeleteMapping(path = "{branchId}")
    public ResponseEntity<?> deleteloaner(@PathVariable("branchId") Long branchId) {
        if (!this.libraryBranchRepository.existsById(branchId)) {
            String message = "Branch not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            this.libraryBranchRepository.deleteById(branchId);
            String message = "Branch deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
