package com.budlib.api.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.budlib.api.exception.NotFoundException;
import com.budlib.api.exception.UserInputException;
import com.budlib.api.model.Loan;
import com.budlib.api.model.Loaner;
import com.budlib.api.model.Transaction;
import com.budlib.api.repository.LoanerRepository;
import com.budlib.api.repository.TransactionRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for {@link Loaner}
 */
@Service
public class LoanerService {

    /**
     * Logger for logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanerService.class);

    /**
     * {@link LoanerRepository} for interacting with database
     */
    private final LoanerRepository loanerRepository;

    /**
     * {@link TransactionRepository} for interacting with database
     */
    private final TransactionRepository transactionRepository;

    /**
     * Constructor
     *
     * @param br {@code LoanerRepository} for database interaction with Loaner
     * @param ts {@code TransactionRepository} for interaction with Transactions
     */
    @Autowired
    public LoanerService(final LoanerRepository lr, final TransactionRepository tr) {

        LOGGER.debug("LoanerService: injected LoanerRepository and TransactionRepository");

        this.loanerRepository = lr;
        this.transactionRepository = tr;
    }

    /**
     * Get {@link Loaner} by ID
     *
     * @param loanerId ID of the Loaner
     * @return Loaner
     */
    public Loaner getLoanerById(final Long loanerId) {

        LOGGER.info("getLoanerById: loanerId = {}", loanerId);

        Optional<Loaner> loanerOptional = this.loanerRepository.findById(loanerId);

        if (loanerOptional.isPresent()) {
            return loanerOptional.get();
        }

        else {
            LOGGER.warn("Loaner with ID={} not found", loanerId);
            return null;
        }
    }

    /**
     * Get list of {@link Transaction} history of the {@link Loaner}
     *
     * @param loanerId Loaner ID whose transaction history is required
     * @return list of Transactions of the Loaner
     * @throws NotFoundException
     */
    public List<Transaction> getTransactionHistory(final Long loanerId) throws NotFoundException {

        LOGGER.info("getTransactionHistory: loanerId = {}", loanerId);

        Loaner l = this.getLoanerById(loanerId);

        if (l == null) {
            LOGGER.error("Loaner with ID={} not found", loanerId);
            throw new NotFoundException(String.format("Loaner with ID=%d not found", loanerId));
        }

        else {
            return l.getTransactionHistory();
        }
    }

    /**
     * Get list of {@link Transaction} history of the {@link Loaner}
     *
     * @param loanerId Loaner ID whose transaction history is required
     * @return list of Transactions of the Loaner
     * @throws NotFoundException
     */
    public List<Loan> getCurrentLoans(final Long loanerId) throws NotFoundException {

        LOGGER.info("getCurrentLoans: loanerId = {}", loanerId);

        Loaner l = this.getLoanerById(loanerId);

        if (l == null) {
            LOGGER.error("Loaner with ID={} not found", loanerId);
            throw new NotFoundException(String.format("Loaner with ID=%d not found", loanerId));
        }

        else {
            return l.getCurrentLoans();
        }
    }

    /**
     * Search {@link Loaner} in the database
     *
     * @param searchParameters parameters to filter the search
     * @return list of Loaners that match the search parameters
     */
    public List<Loaner> searchLoaner(final Map<String, String> searchParameters) {

        LOGGER.info("searchLoaner");

        StringBuilder sb = new StringBuilder();
        searchParameters.forEach((k, v) -> sb.append(k + "=" + v + ", "));

        LOGGER.info("searchLoaner: parameters = {}", sb.toString());

        List<Loaner> allLoaners = this.getAllLoaners();

        if (searchParameters.isEmpty()) {
            LOGGER.debug("No search parameters to filter loaners");
            return allLoaners;
        }

        Set<String> suppliedKeys = searchParameters.keySet();
        LOGGER.debug("{} number of parameters supplied for filtering", suppliedKeys.size());

        Iterator<String> itr = suppliedKeys.iterator();

        while (itr.hasNext()) {
            String key = itr.next();

            switch (key) {
                case "schoolid":
                    allLoaners.removeIf(l -> !this.filterLoanerBySchoolId(l, searchParameters.get(key)));
                    break;

                case "name":
                    allLoaners.removeIf(l -> !this.filterLoanerByFullName(l, searchParameters.get(key)));
                    break;

                case "parentname":
                    allLoaners.removeIf(l -> !this.filterLoanerByParentName(l, searchParameters.get(key)));
                    break;
            }
        }

        return allLoaners;
    }

    /**
     * Save the {@link Loaner} in the database
     *
     * @param loaner Loaner details
     * @return saved Loaner
     * @throws UserInputException
     */
    public Loaner addLoaner(final Loaner loaner) throws UserInputException {

        LOGGER.info("addLoaner: loaner = {}", loaner);

        // reset the id to 0 to prevent overwrite
        loaner.setLoanerId(0L);

        if (this.checkLoanerUniqueness(loaner)) {
            return this.loanerRepository.save(loaner);
        }

        else {
            LOGGER.error("Loaner with ID={} already exists", loaner.getSchoolId());
            throw new UserInputException(String.format("Loaner with ID=%s already exists", loaner.getSchoolId()));
        }
    }

    /**
     * Update the {@link Loaner} in the database
     *
     * @param loaner   Loaner details
     * @param loanerId ID of the Loaner to be updated
     * @return updated Loaner
     * @throws NotFoundException
     * @throws UserInputException
     */
    public Loaner updateLoaner(final Loaner loaner, final Long loanerId) throws NotFoundException, UserInputException {

        LOGGER.info("updateLoaner: loaner = {}, loanerId = {}", loaner, loanerId);

        Loaner l = this.getLoanerById(loanerId);

        if (l == null) {
            LOGGER.error("Loaner with ID={} not found", loanerId);
            throw new NotFoundException(String.format("Loaner with ID=%d not found", loanerId));
        }

        else {
            loaner.setLoanerId(loanerId);

            if (this.checkLoanerUniqueness(loaner)) {
                return this.loanerRepository.save(loaner);
            }

            else {
                LOGGER.error("Loaner with ID={} already exists", loaner.getSchoolId());
                throw new UserInputException(String.format("Loaner with ID=%s already exists", loaner.getSchoolId()));
            }
        }
    }

    /**
     * Delete {@link Loaner} from the database and reset its associated
     * {@link Transaction}s
     *
     * @param loanerId ID of the Loaner to be deleted
     * @throws NotFoundException
     * @throws UserInputException
     */
    public void deleteLoaner(final Long loanerId) throws NotFoundException, UserInputException {

        LOGGER.info("deleteLoaner: loanerId = {}", loanerId);

        Loaner toBeDeleted = this.getLoanerById(loanerId);

        if (toBeDeleted == null) {
            LOGGER.error("Loaner with ID={} not found", loanerId);
            throw new NotFoundException(String.format("Loaner with ID=%d not found", loanerId));
        }

        else if (toBeDeleted.getTotalOutstanding() != 0) {
            LOGGER.error("Cannot delete a loaner with outstanding loans");
            throw new UserInputException("Cannot delete a loaner with outstanding loans");
        }

        else {
            // remove Loaner from all the transactions
            List<Transaction> trnsToBeReset = toBeDeleted.getTransactionHistory();

            for (Transaction trn : trnsToBeReset) {
                trn.setLoaner(null);
                this.transactionRepository.save(trn);
            }

            this.loanerRepository.deleteById(loanerId);
        }
    }

    /**
     * Import the CSV file that was uploaded earlier, with the help of fields
     * mapping provided
     *
     * @param loanerCsv the fields mapping
     * @return number of loaners imported
     * @throws FileNotFoundException
     * @throws UserInputException
     * @throws IOException
     * @throws NullPointerException
     * @throws Throwable
     */
    public int importLoaner(final Map<String, String> loanerCsv)
            throws FileNotFoundException, UserInputException, IOException, NullPointerException, Throwable {

        LOGGER.info("importLoaner: loanerCsv map = {}", loanerCsv);

        int countImported = 0;
        final String uploadCSVFilePath = loanerCsv.get("filename");

        // remove the filename as its not needed anymore, and to prevent hindrance
        loanerCsv.remove("filename");

        try {
            FileReader fileReader = new FileReader(uploadCSVFilePath);

            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();

            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            Iterator<CSVRecord> itr = csvParser.iterator();

            while (itr.hasNext()) {
                CSVRecord record = itr.next();

                Loaner l = this.csvRecordToLoaner(record, loanerCsv);

                if (l == null) {
                    continue;
                }

                else {
                    this.loanerRepository.save(l);
                    countImported++;
                }
            }

            csvParser.close();

            return countImported;
        }

        catch (FileNotFoundException e) {
            LOGGER.error("Error in retrieving uploaded file");
            throw new FileNotFoundException("Error in retrieving uploaded file");
        }

        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Incorrect mapping provided");
            throw new UserInputException("Incorrect mapping provided");
        }

        catch (IOException e) {
            LOGGER.error("Error in parsing uploaded file");
            throw new IOException("Error in parsing uploaded file");
        }

        catch (NullPointerException e) {
            LOGGER.error("All nulls are not handled");
            throw new NullPointerException("All nulls are not handled");
        }

        catch (Throwable e) {
            LOGGER.error("Something unexpected happened", e);
            throw new Throwable("Something unexpected happened");
        }
    }

    /**
     * Get the list of all {@link Loaner} in the database
     *
     * @return list of all Loaners
     */
    private List<Loaner> getAllLoaners() {
        LOGGER.debug("getAllLoaners");
        return this.loanerRepository.findAll();
    }

    /**
     * Check if the {@link Loaner} contains the given school ID
     *
     * @param loaner Loaner to check
     * @param filter school ID to check
     * @return true if the Loaner contains the school ID, false otherwise
     */
    private boolean filterLoanerBySchoolId(final Loaner loaner, final String filter) {

        LOGGER.debug("filterLoanerBySchoolId: filter = {}", filter);

        String givenSchoolId = filter.toLowerCase();

        if (loaner.getSchoolId() != null && loaner.getSchoolId().toLowerCase().contains(givenSchoolId)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check if the {@link Loaner} contains the given name
     *
     * @param loaner Loaner to check
     * @param filter name to check
     * @return true if the Loaner contains the name, false otherwise
     */
    private boolean filterLoanerByFullName(final Loaner loaner, final String filter) {

        LOGGER.debug("filterLoanerByFullName: filter = {}", filter);

        String givenName = filter.toLowerCase();

        if (loaner.getFullName() != null && loaner.getFullName().toLowerCase().contains(givenName)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check if the {@link Loaner} contains the given parent name
     *
     * @param loaner Loaner to check
     * @param filter parent name to check
     * @return true if the Loaner contains the parent name, false otherwise
     */
    private boolean filterLoanerByParentName(final Loaner loaner, final String filter) {

        LOGGER.debug("filterLoanerByParentName: filter = {}", filter);

        String givenParentName = filter.toLowerCase();

        if (loaner.getMotherName() != null && loaner.getMotherName().toLowerCase().contains(givenParentName)) {
            return true;
        }

        else if (loaner.getFatherName() != null && loaner.getFatherName().toLowerCase().contains(givenParentName)) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Check if the {@link Loaner} exists in the system. Works only if its
     * {@code schoolId} is not null or not empty.
     *
     * @param loaner Loaner to checked for uniqueness, usually when adding a new
     *               Loaner or updating an existing one
     * @return true if unique, false otherwise
     */
    private boolean checkLoanerUniqueness(final Loaner loaner) {

        LOGGER.info("checkLoanerUniqueness: loaner = {}", loaner);

        if (loaner.getSchoolId() == null || loaner.getSchoolId().equals("")) {
            return true;
        }

        List<Loaner> allLoaners = this.getAllLoaners();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getLoanerId() == loaner.getLoanerId()) {
                continue;
            }

            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().equalsIgnoreCase(loaner.getSchoolId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifies if the CSV record if empty or not. A record is empty when all the
     * intended details are blank.
     *
     * @param record The CSV record to check for emptiness
     * @return {@code true} if empty, {@code false} false otherwise
     */
    private boolean checkEmptyRecord(final CSVRecord record) {

        LOGGER.debug("checkEmptyRecord: record = {}", record);

        Iterator<String> itr = record.iterator();

        while (itr.hasNext()) {
            String str = itr.next();

            if (str != null && str.length() != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts CSV record to {@link Loaner}, with the field mapping provided by
     * loanerCsv
     *
     * @param record    The CSV Record fetched from the CSV file
     * @param loanerCsv Map of fields
     * @return Loaner object
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    private Loaner csvRecordToLoaner(final CSVRecord record, Map<String, String> loanerCsv)
            throws NumberFormatException, NullPointerException {

        LOGGER.info("csvRecordToLoaner: record = {}, loanerCsv map = {}", record, loanerCsv);

        if (this.checkEmptyRecord(record)) {
            return null;
        }

        Loaner l = new Loaner();
        l.setLoanerId(0);

        // all the below are actually integers in String format
        String schoolId = loanerCsv.get("schoolId");
        String isStudent = loanerCsv.get("isStudent");
        String email = loanerCsv.get("email");
        String salutation = loanerCsv.get("salutation");
        String firstName = loanerCsv.get("first_name");
        String middleName = loanerCsv.get("middle_name");
        String lastName = loanerCsv.get("last_name");
        String motherName = loanerCsv.get("mother_name");
        String fatherName = loanerCsv.get("father_name");

        // set school ID
        if (schoolId != null) {
            l.setSchoolId(record.get(Integer.parseInt(schoolId)));
        }

        // set isStudent
        if (isStudent != null) {
            l.setStudent(Boolean.valueOf(record.get(Integer.parseInt(isStudent))));
        }

        // set email
        if (email != null) {
            l.setEmail(record.get(Integer.parseInt(email)));
        }

        // set salutation
        if (salutation != null) {
            l.setSalutation(record.get(Integer.parseInt(salutation)));
        }

        // set first name
        if (firstName != null) {
            l.setFirstName(record.get(Integer.parseInt(firstName)));
        }

        // set middle name
        if (middleName != null) {
            l.setMiddleName(record.get(Integer.parseInt(middleName)));
        }

        // set last name
        if (lastName != null) {
            l.setLastName(record.get(Integer.parseInt(lastName)));
        }

        // set mother name
        if (motherName != null) {
            l.setMotherName(record.get(Integer.parseInt(motherName)));
        }

        // set father name
        if (fatherName != null) {
            l.setFatherName(record.get(Integer.parseInt(fatherName)));
        }

        return l;
    }
}
