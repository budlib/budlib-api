package com.budlib.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.budlib.api.model.Book;
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
     * Get the {@link Tag} by ID
     *
     * @param tagId ID of the Tag
     * @return Tag
     */
    public Tag getTagById(final Long tagId) {

        LOGGER.info("getTagById: tagId = {}", tagId);

        Optional<Tag> tagOptional = this.tagRepository.findById(tagId);

        if (tagOptional.isPresent()) {
            return tagOptional.get();
        }

        else {
            LOGGER.warn("Tag with ID={} not found", tagId);
            return null;
        }
    }

    /**
     * Save the {@link Tag} to the database if its doesn't already exist
     *
     * @param t Tag details
     * @return saved Tag
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
     * Search {@link Tag} in the database
     *
     * @param searchParameters parameters to filter the search
     * @return list of Tags that match the search parameters
     */
    public List<Tag> searchTag(final Map<String, String> searchParameters) {

        LOGGER.info("searchTag");

        StringBuilder sb = new StringBuilder();
        searchParameters.forEach((k, v) -> sb.append(k + "=" + v + ", "));

        LOGGER.info("searchTag: parameters = {}", sb.toString());

        List<Tag> allTags = this.getAllTags();

        if (searchParameters.isEmpty()) {
            LOGGER.debug("No search parameters to filter tags");
            return allTags;
        }

        Set<String> suppliedKeys = searchParameters.keySet();
        LOGGER.debug("{} number of parameters supplied for filtering", suppliedKeys.size());

        Iterator<String> itr = suppliedKeys.iterator();

        while (itr.hasNext()) {
            String key = itr.next();

            switch (key) {
                case "tagname":
                    LOGGER.debug("Filtering by tagname");
                    allTags = this.filterTagsByTagName(allTags, searchParameters.get(key));
                    break;
            }
        }

        return allTags;
    }

    /**
     * Remove all unused tags from the database. Unused tags are the ones that no
     * {@link Book} is referring to
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
                LOGGER.debug("Removing tag = {}", t.toString());
                this.tagRepository.delete(t);

                deleteCount++;
            }
        }

        return deleteCount;
    }

    /**
     * Get the list of all {@link Tag} in the database
     *
     * @return list of all Tags
     */
    private List<Tag> getAllTags() {
        LOGGER.debug("getAllTags");
        return this.tagRepository.findAll();
    }

    /**
     * Search the {@link Tag} by tag name.
     *
     * @param allTags list of all tags
     * @param sT      search term
     * @return filtered list of tags with name meeting the search term
     */
    private List<Tag> filterTagsByTagName(final List<Tag> allTags, final String sT) {

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
}
