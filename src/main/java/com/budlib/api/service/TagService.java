package com.budlib.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.budlib.api.model.Tag;
import com.budlib.api.repository.TagRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for {@link Tag}
 */
@Service
public class TagService {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TagService.class);

    /**
     * {@link TagRepository} for interacting with database
     */
    private final TagRepository tagRepository;

    /**
     * Constructor
     *
     * @param tr {@code TagRepository} for database interaction with Tag
     */
    @Autowired
    public TagService(final TagRepository tr) {

        LOGGER.debug("TagService: injected TagRepository");

        this.tagRepository = tr;
    }

    /**
     * Saves the {@link Tag} to the database if its doesn't already exists.
     *
     * @param t The {@link Tag} be saved to the database
     * @return saved {@link Tag}
     */
    public Tag addTag(final Tag t) {

        LOGGER.info("addTag: tag = {}", t);

        // reset the id to 0 to prevent overwrite
        t.setTagId(0L);

        Iterator<Tag> itr = this.tagRepository.findAll().listIterator();

        while (itr.hasNext()) {
            Tag eachTag = itr.next();

            if (t.equals(eachTag)) {
                return eachTag;
            }
        }

        return this.tagRepository.save(t);
    }

    /**
     * Get the {@link Tag} by its ID
     *
     * @param id ID of the {@link Tag}
     * @return {@link Tag}
     */
    public Tag getTagById(final Long id) {

        LOGGER.info("getTagById: tagId = {}", id);

        Optional<Tag> tagOptional = this.tagRepository.findById(id);

        if (tagOptional.isPresent()) {
            return tagOptional.get();
        }

        return null;
    }

    /**
     * Search the {@link Tag} by tag name.
     *
     * @param allTags list of all tags
     * @param sT      search term
     * @return filtered list of tags with name meeting the search term
     */
    private List<Tag> searchTagByName(List<Tag> allTags, String sT) {

        LOGGER.info("searchTagByName: searchTerm = {}", sT);

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
     * Search and fetch all tags meeting search criteria
     *
     * @param searchBy   where to search
     * @param searchTerm what to search
     * @return list of tags meeting search criteria
     */
    public List<Tag> searchTag(final String searchBy, final String searchTerm) {

        LOGGER.info("searchTag: searchBy = {}, searchTerm = {}", searchBy, searchTerm);

        List<Tag> allTags = this.tagRepository.findAll();
        List<Tag> noTags = new ArrayList<>();

        try {
            if (searchBy == null || searchTerm == null || searchBy.equals("") || searchTerm.equals("")) {
                LOGGER.debug("searchTag: no parameters found, fetching all tags");
                return allTags;
            }

            else if (searchBy.equalsIgnoreCase("tagname")) {
                return this.searchTagByName(allTags, searchTerm);
            }

            else {
                LOGGER.debug("searchTag: invalid parameters, result will be empty");
                return noTags;
            }
        }

        catch (Exception e) {
            LOGGER.warn("searchTag: invalid parameters, result will be empty");
            return noTags;
        }
    }

    /**
     * Remove all unused tags from the database. Unused tags are the ones that no
     * {@link Book} is referring to.
     *
     * @return number of tags removed
     */
    public int removeUnusedTags() {

        LOGGER.info("removeUnusedTags");

        List<Tag> allTags = this.tagRepository.findAll();

        int deleteCount = 0;

        for (int i = 0; i < allTags.size(); i++) {
            Tag t = allTags.get(i);

            int size = t.getBooks().size();

            if (size == 0) {
                LOGGER.debug("removeUnusedTags: Removing tag = {}", t.toString());
                this.tagRepository.delete(t);

                deleteCount++;
            }
        }

        return deleteCount;
    }
}
