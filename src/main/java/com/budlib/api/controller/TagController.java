package com.budlib.api.controller;

import com.budlib.api.model.*;
import com.budlib.api.repository.*;
import com.budlib.api.response.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for book tags
 */
@CrossOrigin
@RestController
@RequestMapping("api/tags")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    /**
     * Search the tags by id
     *
     * @param id tag id
     * @return list of tags with id; the list would have utmost one element
     */
    private List<Tag> searchTagById(Long id) {
        Optional<Tag> tagOptional = this.tagRepository.findById(id);

        if (tagOptional.isPresent()) {
            List<Tag> searchResults = new ArrayList<>();
            searchResults.add(tagOptional.get());
            return searchResults;
        }

        else {
            return null;
        }
    }

    /**
     * Search tags by tag name
     *
     * @param allTags list of all tags
     * @param sT      search term
     * @return filtered list of tags with name meeting the search term
     */
    private List<Tag> searchTagByName(List<Tag> allTags, String sT) {
        String searchTerm = sT.toLowerCase();
        List<Tag> searchResults = new ArrayList<>();

        for (Tag eachTag : allTags) {
            if (eachTag.getTagName() != null && eachTag.getTagName().toLowerCase().contains(searchTerm)) {
                searchResults.add(eachTag);
            }
        }

        return searchResults;
    }

    /**
     * Endpoint for GET - fetch tag by id
     *
     * @param id tag id
     * @return tag
     */
    @GetMapping(path = "{tagId}")
    public ResponseEntity<?> getTagById(@PathVariable("tagId") Long id) {
        List<Tag> t = this.searchTagById(id);

        if (t == null) {
            String message = "Tag not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(t.get(0));
        }
    }

    /**
     * Endpoint for GET - search and fetch all tags meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of tags meeting search criteria
     */
    @GetMapping()
    public ResponseEntity<?> searchTag(@RequestParam(name = "searchBy", required = false) String searchBy,
            @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        List<Tag> allTags = this.tagRepository.findAll();

        try {
            if (searchBy == null || searchTerm == null) {
                return ResponseEntity.status(HttpStatus.OK).body(allTags);
            }

            else if (searchBy.equals("") || searchTerm.equals("")) {
                return ResponseEntity.status(HttpStatus.OK).body(allTags);
            }

            else if (searchBy.equalsIgnoreCase("id")) {
                return ResponseEntity.status(HttpStatus.OK).body(this.searchTagById(Long.valueOf(searchTerm)));
            }

            else if (searchBy.equalsIgnoreCase("name")) {
                return ResponseEntity.status(HttpStatus.OK).body(this.searchTagByName(allTags, searchTerm));
            }

            else {
                // String message = "Invalid tag search operation";
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                // .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                return ResponseEntity.status(HttpStatus.OK).body(allTags);
            }
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(allTags);
        }
    }

    /**
     * Endpoint for POST - save the tag in db
     *
     * @param t tag details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody Tag t) {
        // reset the id to 0 to prevent overwrite
        t.setTagId(0L);

        Iterator<Tag> i = this.tagRepository.findAll().listIterator();

        while (i.hasNext()) {
            if (t.getTagName().equalsIgnoreCase((i.next().getTagName()))) {
                String message = "Tag already exists";
                return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
            }
        }

        this.tagRepository.save(t);

        String message = "Tag added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for DELETE - delete all unused tags from db
     *
     * @param bookId book id
     */
    @DeleteMapping(path = "cleanup")
    public ResponseEntity<?> deleteUnusedTags() {
        List<Tag> allTags = this.tagRepository.findAll();

        int count = 0;

        for (int i = 0; i < allTags.size(); i++) {
            Tag t = allTags.get(i);

            int size = t.getBooks().size();
            System.out.printf("Tag %s used by %d books %n", t.getTagName(), size);

            if (size == 0) {
                this.tagRepository.delete(t);

                count++;
            }
        }

        String message = String.format("Tags cleaned up successfully. %d tags removed.", count);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }
}
