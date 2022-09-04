package com.budlib.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.budlib.api.enums.LibrarianRole;
import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Librarian;
import com.budlib.api.model.Transaction;
import com.budlib.api.repository.LibrarianRepository;
import com.budlib.api.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for {@link Librarian}
 */
@Service
public class LibrarianService implements UserDetailsService {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LibrarianService.class);

    /**
     * {@link LibrarianRepository} for interacting with database
     */
    private final LibrarianRepository librarianRepository;

    /**
     * {@link TransactionRepository} for interacting with database
     */
    private final TransactionRepository transactionRepository;

    /**
     * Constructor
     *
     * @param lr {@code LibrarianRepository} for database interaction with Librarian
     * @param ts {@code TransactionRepository} for interaction with Transactions
     */
    @Autowired
    public LibrarianService(final LibrarianRepository lr, final TransactionRepository tr) {

        LOGGER.debug("LibrarianService: injected LibrarianRepository and TransactionRepository");

        this.librarianRepository = lr;
        this.transactionRepository = tr;
    }

    /**
     * Get {@link Librarian} by ID
     *
     * @param librarianId ID of the Librarian
     * @return Librarian
     */
    public Librarian getLibrarianById(final Long librarianId) {

        LOGGER.info("getLibrarianById: librarianId = {}", librarianId);

        Optional<Librarian> librarianOptional = this.librarianRepository.findById(librarianId);

        if (librarianOptional.isPresent()) {
            return librarianOptional.get();
        }

        else {
            LOGGER.warn("Librarian with ID={} not found", librarianId);
            return null;
        }
    }

    /**
     * Get {@link Librarian} by email
     *
     * @param email email of the Librarian
     * @return Librarian
     */
    public Librarian getLibrarianByEmail(final String email) {

        LOGGER.info("getLibrarianByEmail: email = {}", email);

        List<Librarian> allLibrarians = this.getAllLibrarians();

        for (Librarian l : allLibrarians) {
            if (l.getEmail().equalsIgnoreCase(email)) {
                return l;
            }
        }

        LOGGER.warn("Librarian with email=\"{}\" not found", email);
        return null;
    }

    /**
     * Get list of {@link Transaction} coordinated by the {@link Librarian}
     *
     * @param librarianId Librarian ID whose coordinated history is required
     * @return list of Transactions coordinated by the Librarian
     * @throws NotFoundException
     */
    public List<Transaction> getCoordinationHistory(final Long librarianId) throws NotFoundException {

        LOGGER.info("getCoordinationHistory: librarianId = {}", librarianId);

        Librarian l = this.getLibrarianById(librarianId);

        if (l == null) {
            LOGGER.error("Librarian with ID={} not found", librarianId);
            throw new NotFoundException(String.format("Librarian with ID=%d not found", librarianId));
        }

        else {
            return l.getTransactionHistory();
        }
    }

    /**
     * Search {@link Librarian} in the database
     *
     * @param searchParameters parameters to filter the search
     * @return list of Librarians that match the search parameters
     */
    public List<Librarian> searchLibrarian(final Map<String, String> searchParameters) {

        LOGGER.info("searchLibrarian");

        StringBuilder sb = new StringBuilder();
        searchParameters.forEach((k, v) -> sb.append(k + "=" + v + ", "));

        LOGGER.info("searchLibrarian: parameters = {}", sb.toString());

        List<Librarian> allLibrarians = this.getAllLibrarians();

        if (searchParameters.isEmpty()) {
            LOGGER.debug("No search parameters to filter Librarians");
            return allLibrarians;
        }

        Set<String> suppliedKeys = searchParameters.keySet();
        LOGGER.debug("{} number of parameters supplied for filtering", suppliedKeys.size());

        Iterator<String> itr = suppliedKeys.iterator();

        while (itr.hasNext()) {
            String key = itr.next();

            switch (key) {
                case "email":
                    allLibrarians.removeIf(l -> !this.filterLibrarianByEmail(l, searchParameters.get(key)));
                    break;

                case "username":
                    allLibrarians.removeIf(l -> !this.filterLibrarianByUsername(l, searchParameters.get(key)));
                    break;

                case "name":
                    allLibrarians.removeIf(l -> !this.filterLibrarianByFullName(l, searchParameters.get(key)));
                    break;
            }
        }

        return allLibrarians;
    }

    /**
     * Save the {@link Librarian} in the database
     *
     * @param librarian Librarian details
     * @return saved Librarian
     * @throws UserInputException
     */
    public Librarian addLibrarian(final Librarian librarian) throws UserInputException {

        LOGGER.info("addLibrarian: librarian = {}", librarian);

        // reset the id to 0 to prevent overwrite
        librarian.setLibrarianId(0L);

        String[] checkDetails = this.checkSuppliedDetails(librarian);

        if (checkDetails[0].equals("true")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            librarian.setPassword(encoder.encode(librarian.getPassword()));

            return this.librarianRepository.save(librarian);
        }

        else {
            LOGGER.error(checkDetails[1]);
            throw new UserInputException(checkDetails[1]);
        }
    }

    /**
     * Update the {@link Librarian} in the database
     *
     * @param librarian   Librarian details
     * @param librarianId ID of the Librarian to be updated
     * @return updated Librarian
     * @throws NotFoundException
     * @throws UserInputException
     */
    public Librarian updateLibrarian(final Librarian librarian, final Long librarianId)
            throws NotFoundException, UserInputException {

        LOGGER.info("updateLibrarian: librarian = {}, librarianId = {}", librarian, librarianId);

        Librarian l = this.getLibrarianById(librarianId);

        if (l == null) {
            LOGGER.error("Librarian with ID={} not found", librarianId);
            throw new NotFoundException(String.format("Librarian with ID=%d not found", librarianId));
        }

        else {
            librarian.setLibrarianId(librarianId);
            librarian.setPassword(l.getPassword());

            String[] checkDetails = this.checkSuppliedDetails(librarian);

            if (checkDetails[0].equals("true")) {
                return this.librarianRepository.save(librarian);
            }

            else {
                LOGGER.error(checkDetails[1]);
                throw new UserInputException(checkDetails[1]);
            }
        }
    }

    /**
     * Update the password of the {@link Librarian} in the database
     *
     * @param librarian   Librarian details
     * @param librarianId ID of the Librarian to be updated
     * @return updated Librarian
     * @throws NotFoundException
     * @throws UserInputException
     */
    public Librarian changePassword(final Librarian librarian, final Long librarianId)
            throws NotFoundException, UserInputException {

        LOGGER.info("changePassword: librarian = {}, librarianId = {}", librarian, librarianId);

        Librarian l = this.getLibrarianById(librarianId);

        if (l == null) {
            LOGGER.error("Librarian with ID={} not found", librarianId);
            throw new NotFoundException(String.format("Librarian with ID=%d not found", librarianId));
        }

        else {
            // temporarily set unencrypted password for checkDetails method
            l.setPassword(librarian.getPassword());

            String[] checkDetails = this.checkSuppliedDetails(l);

            if (checkDetails[0].equals("true")) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                l.setPassword(encoder.encode(l.getPassword()));
                return this.librarianRepository.save(l);
            }

            else {
                LOGGER.error(checkDetails[1]);
                throw new UserInputException(checkDetails[1]);
            }
        }
    }

    /**
     * Delete {@link Librarian} from the database and reset its associated
     * {@link Transaction}s
     *
     * @param librarianId ID of the Librarian to be deleted
     * @throws NotFoundException
     * @throws UserInputException
     */
    public void deleteLibrarian(final Long librarianId, final Long deleterId)
            throws NotFoundException, UserInputException {

        LOGGER.info("deleteLibrarian: librarianId = {}, deleterId = {}", librarianId, deleterId);

        if (librarianId == deleterId) {
            LOGGER.error("You cannot delete your own account");
            throw new UserInputException("You cannot delete your own account");
        }

        Librarian deleterLibrarian = this.getLibrarianById(deleterId);

        if (deleterLibrarian == null) {
            LOGGER.error("Delete request by unknown Librarian with ID={}", deleterId);
            throw new UserInputException(String.format("Delete request by unknown Librarian with ID=%d", deleterId));
        }

        else if (deleterLibrarian.getRole().equals(LibrarianRole.FACULTY)) {
            LOGGER.error("You dont have necessary permissions");
            throw new UserInputException("You dont have necessary permission");
        }

        Librarian toBeDeleted = this.getLibrarianById(librarianId);

        if (toBeDeleted == null) {
            LOGGER.error("Librarian with ID={} not found", librarianId);
            throw new NotFoundException(String.format("Librarian with ID=%d not found", librarianId));
        }

        else {
            // remove Librarian from all the transactions
            List<Transaction> trnsToBeReset = toBeDeleted.getTransactionHistory();

            for (Transaction trn : trnsToBeReset) {
                trn.setLibrarian(null);
                this.transactionRepository.save(trn);
            }

            this.librarianRepository.deleteById(librarianId);
        }
    }

    /**
     * Count the number of administrators in the system
     *
     * @return Number of library administrators
     */
    public int getAdminCount() {

        LOGGER.info("getAdminCount");

        int count = 0;
        List<Librarian> allLibrarians = this.getAllLibrarians();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getRole().equals(LibrarianRole.ADMIN)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Locates Librarian by email
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {

        LOGGER.info("loadUserByUsername: username = {}", username);

        List<Librarian> allLibrarians = this.getAllLibrarians();

        for (Librarian eachLibrarian : allLibrarians) {
            if (eachLibrarian.getEmail().equalsIgnoreCase(username)) {
                // return new User(username, eachLibrarian.getPassword(),
                // this.getAuthorities(eachLibrarian));

                UserDetails user = User.withUsername(username).password(eachLibrarian.getPassword())
                        .roles(eachLibrarian.getRole().toString())
                        // .authorities(this.getAuthorities(eachLibrarian)).build();
                        .build();

                return user;
            }
        }

        return null;
    }

    /**
     * Get the list of authorities for the given {@link Librarian}
     *
     * @param l Librarian details
     * @return list of authorities
     */
    @SuppressWarnings("unused")
    private Collection<GrantedAuthority> getAuthorities(final Librarian l) {

        LOGGER.debug("getAuthorities: librarian = {}", l);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(l.getRole().toString()));

        return authorities;
    }

    /**
     * Get the list of all {@link Librarian} in the database
     *
     * @return list of all Librarians
     */
    private List<Librarian> getAllLibrarians() {
        LOGGER.debug("getAllLibrarians");
        return this.librarianRepository.findAll();
    }

    /**
     * Check if the {@link Librarian} contains the given email
     *
     * @param librarian Librarian to check
     * @param filter    email to check
     * @return true if the Librarian contains the email, false otherwise
     */
    private boolean filterLibrarianByEmail(final Librarian librarian, final String filter) {

        LOGGER.debug("filterLibrarianByEmail: filter = {}", filter);

        String givenEmail = filter.toLowerCase();

        if (librarian.getEmail() != null && librarian.getEmail().toLowerCase().contains(givenEmail)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check if the {@link Librarian} contains the given username
     *
     * @param librarian Librarian to check
     * @param filter    username to check
     * @return true if the Librarian contains the username, false otherwise
     */
    private boolean filterLibrarianByUsername(final Librarian librarian, final String filter) {

        LOGGER.debug("filterLibrarianByUsername: filter = {}", filter);

        String givenUsername = filter.toLowerCase();

        if (librarian.getUserName() != null && librarian.getUserName().toLowerCase().contains(givenUsername)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check if the {@link Librarian} contains the given name
     *
     * @param librarian Librarian to check
     * @param filter    name to check
     * @return true if the Librarian contains the name, false otherwise
     */
    private boolean filterLibrarianByFullName(final Librarian librarian, final String filter) {

        LOGGER.debug("filterLibrarianByFullName: filter = {}", filter);

        String givenName = filter.toLowerCase();

        if (librarian.getFullName() != null && librarian.getFullName().toLowerCase().contains(givenName)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check the details of the librarian supplied
     *
     * @param l librarian details
     * @return response string, [0] contains true if all good or false if details
     *         are incorrect. [1] contains reason for incorrect details
     */
    private String[] checkSuppliedDetails(final Librarian l) {

        LOGGER.debug("checkSuppliedDetails: librarian = {}", l);

        String[] response = new String[2];

        // these values will change in case of error
        response[0] = "true";
        response[1] = "All good";

        List<Librarian> allLibrarians = this.getAllLibrarians();

        String suppliedUsername = l.getUserName();
        String suppliedEmail = l.getEmail();
        String suppliedPassword = l.getPassword();

        // RFC 5322 regex check
        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if (suppliedUsername == null || suppliedUsername.equals("")) {
            response[0] = "false";
            response[1] = "Username cannot be empty";
        }

        else if (suppliedPassword == null || suppliedPassword.equals("")) {
            response[0] = "false";
            response[1] = "Password cannot be empty";
        }

        else if (suppliedEmail == null || suppliedEmail.equals("")) {
            response[0] = "false";
            response[1] = "Email cannot be empty";
        }

        else if (!suppliedEmail.matches(emailRegex)) {
            response[0] = "false";
            response[1] = "Invalid email supplied";
        }

        else {
            for (Librarian eachLibrarian : allLibrarians) {
                if (eachLibrarian.getLibrarianId() == l.getLibrarianId()) {
                    continue;
                }

                if (eachLibrarian.getUserName() != null
                        && eachLibrarian.getUserName().equalsIgnoreCase(suppliedUsername)) {
                    response[0] = "false";
                    response[1] = "Username already taken";
                    break;
                }

                if (eachLibrarian.getEmail() != null && eachLibrarian.getEmail().equalsIgnoreCase(suppliedEmail)) {
                    response[0] = "false";
                    response[1] = "Email already taken";
                    break;
                }
            }
        }

        return response;
    }
}
