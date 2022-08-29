package com.budlib.api.controller;

import java.util.HashMap;
import java.util.Map;

import com.budlib.api.model.Book;
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

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    /**
     * {@link TagService} for interacting with Tags
     */
    private final TagService tagService;

    /**
     * Constructor
     *
     * @param ts {@code TagService} for interaction with Tags
     */
    @Autowired
    public TagController(final TagService ts) {

        LOGGER.debug("TagController: injected TagService");

        this.tagService = ts;
    }

    /**
     * Get the {@link Tag} by ID
     *
     * @param tagId ID of the Tag
     * @return Tag
     */
    @GetMapping(path = "{tagId}")
    public ResponseEntity<?> getTagById(@PathVariable("tagId") Long tagId) {

        LOGGER.info("getTagById: tagId = {}", tagId);

        Tag t = this.tagService.getTagById(tagId);

        if (t == null) {
            String message = "Tag not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            return ResponseEntity.status(HttpStatus.OK).body(t);
        }
    }

    /**
     * Search {@link Tag} in the database
     *
     * @param tagName name of the Tag
     * @return list of Tags that match the filters
     */
    @GetMapping()
    public ResponseEntity<?> searchTag(@RequestParam(name = "tagname", required = false) String tagName) {

        LOGGER.info("searchBook: parameters = tagName = {}", tagName);

        Map<String, String> parameters = new HashMap<>();

        if (tagName != null) {
            parameters.put("tagname", tagName);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.tagService.searchTag(parameters));
    }

    /**
     * Save the {@link Tag} in the database
     *
     * @param tag Tag details
     * @return the message
     */
    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody Tag tag) {

        LOGGER.info("addTag: tag = {}", tag);

        this.tagService.addTag(tag);

        String message = "Tag added successfully";
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }

    /**
     * Remove all unused tags from the database. Unused tags are the ones that no
     * {@link Book} is referring to
     *
     * @return the message
     */
    @DeleteMapping(path = "cleanup")
    public ResponseEntity<?> removeUnusedTags() {

        LOGGER.info("removeUnusedTags");

        int deleteCount = this.tagService.removeUnusedTags();

        String message = String.format("Tags cleaned up successfully. %d tags removed.", deleteCount);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
    }
}
