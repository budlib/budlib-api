package com.budlib.api.controller;

import com.budlib.api.model.Tag;
import com.budlib.api.response.ErrorBody;
import com.budlib.api.service.TagService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for book tags
 */
@CrossOrigin
@RestController
@RequestMapping("api/tags")
public class TagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    @Autowired
    public TagController(final TagService ts) {

        LOGGER.debug("TagController");

        this.tagService = ts;
    }

    /**
     * Endpoint for GET - fetch tag by id
     *
     * @param id tag id
     * @return tag
     */
    @GetMapping(path = "{tagId}")
    public ResponseEntity<?> getTagById(@PathVariable("tagId") Long id) {

        LOGGER.info("getTagById: tagId = {}", id);

        Tag t = this.tagService.getTagById(id);

        if (t == null) {
            String message = "Tag not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(t);
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

        LOGGER.info("searchTag: searchBy = {}, searchTerm = {}", searchBy, searchTerm);

        return ResponseEntity.status(HttpStatus.OK).body(this.tagService.searchTag(searchBy, searchTerm));
    }

    /**
     * Endpoint for POST - save the tag in db
     *
     * @param t tag details in json
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody Tag t) {

        LOGGER.info("addTag: tag = {}", t);

        this.tagService.addTag(t);

        String message = "Tag added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Endpoint for DELETE - delete all unused tags from db
     *
     * @param bookId book id
     */
    @DeleteMapping(path = "cleanup")
    public ResponseEntity<?> removeUnusedTags() {

        LOGGER.info("removeUnusedTags");

        int deleteCount = this.tagService.removeUnusedTags();

        String message = String.format("Tags cleaned up successfully. %d tags removed.", deleteCount);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }
}
