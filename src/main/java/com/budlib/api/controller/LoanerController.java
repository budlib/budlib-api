package com.budlib.api.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.budlib.api.model.Loan;
import com.budlib.api.model.Loaner;
import com.budlib.api.model.Transaction;
import com.budlib.api.repository.LoanerRepository;
import com.budlib.api.repository.TransactionRepository;
import com.budlib.api.response.ErrorBody;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for loaner
 */
@CrossOrigin
@RestController
@RequestMapping("api/loaners")
public class LoanerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanerController.class);

    private final LoanerRepository loanerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public LoanerController(final LoanerRepository lr, final TransactionRepository tr) {

        LOGGER.debug("LoanerController");

        this.loanerRepository = lr;
        this.transactionRepository = tr;
    }

    /**
     * Search the loaner by id
     *
     * @param id loaner id
     * @return list of loaners with id; the list would have utmost one element
     */
    private List<Loaner> searchLoanerById(Long id) {

        LOGGER.info("searchLoanerById: loanerId = {}", id);

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

        LOGGER.info("searchLoanerBySchoolId: searchTerm = {}", sT);

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

        LOGGER.info("searchLoanerByName: searchTerm = {}", sT);

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

        LOGGER.info("searchStudentByParentName: searchTerm = {}", sT);

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

        LOGGER.info("getLoanerById: loanerId = {}", id);

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
     * Endpoint for GET - fetch the transaction history of the Loaner
     *
     * @param id Loaner ID whose transaction history is required
     * @return list of transactions
     */
    @GetMapping(path = "{loanerId}/history")
    public ResponseEntity<?> getTransactionHistory(@PathVariable("loanerId") Long id) {

        LOGGER.info("getTransactionHistory: loanerId = {}", id);

        List<Loaner> l = this.searchLoanerById(id);

        if (l == null) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            List<Transaction> trnHistory = l.get(0).getTransactionHistory();
            return ResponseEntity.status(HttpStatus.OK).body(trnHistory);
        }
    }

    /**
     * Endpoint for GET - fetch the current outstanding books of the loaner
     *
     * @param id Loaner ID whose current loans is required
     * @return list of loans
     */
    @GetMapping(path = "{loanerId}/loans")
    public ResponseEntity<?> getCurrentLoans(@PathVariable("loanerId") Long id) {

        LOGGER.info("getCurrentLoans: loanerId = {}", id);

        List<Loaner> l = this.searchLoanerById(id);

        if (l == null) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            List<Loan> currentLoans = l.get(0).getCurrentLoans();
            return ResponseEntity.status(HttpStatus.OK).body(currentLoans);
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

        LOGGER.info("searchLoaner: searchBy = {}, searchTerm = {}", searchBy, searchTerm);

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        try {
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
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.searchStudentByParentName(allLoaners, searchTerm));
            }

            else {
                // String message = "Invalid loaner search operation";
                // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                // .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
                return ResponseEntity.status(HttpStatus.OK).body(allLoaners);
            }
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(allLoaners);
        }
    }

    /**
     * Check if the loaner exists in the system. Works only if Loaner.schoolId is
     * not null or empty. This method exists in TransactionController as well.
     *
     * @param l Loaner to be added or updated
     * @return true if unique, false otherwise
     */
    private boolean checkLoanerUniqueness(Loaner l) {

        LOGGER.info("checkLoanerUniqueness: loaner = {}", l);

        if (l.getSchoolId() == null || l.getSchoolId().equals("")) {
            return true;
        }

        List<Loaner> allLoaners = this.loanerRepository.findAll();

        for (Loaner eachLoaner : allLoaners) {
            if (eachLoaner.getLoanerId() == l.getLoanerId()) {
                continue;
            }

            if (eachLoaner.getSchoolId() != null && eachLoaner.getSchoolId().equalsIgnoreCase(l.getSchoolId())) {
                return false;
            }
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

        LOGGER.info("addLoaner: loaner = {}", l);

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
     * Verifies if the CSV record if empty or not. A record is empty when all the
     * intended details are blank.
     *
     * @param record The CSV record to check for emptiness
     * @return {@code true} if empty, {@code false} false otherwise
     */
    private boolean checkEmptyRecord(CSVRecord record) {

        LOGGER.info("checkEmptyRecord: record = {}", record);

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
     * Converts CSV record to Loaner object, with the field mapping provided by
     * loanerCsv
     *
     * @param record    The CSV Record fetched from the CSV file
     * @param loanerCsv
     * @return book object
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    private Loaner csvRecordToLoaner(final CSVRecord record, HashMap<String, String> loanerCsv)
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

    /**
     * Import the CSV file that was uploaded, with the help of fields mapping
     * provided
     *
     * @param loanerCsv the fields mapping
     * @return message
     */
    @PostMapping(path = "import")
    public ResponseEntity<?> importLoaners(@RequestBody HashMap<String, String> loanerCsv) {

        LOGGER.info("importLoaners: loanerCsv map = {}", loanerCsv);

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

                else if (this.checkLoanerUniqueness(l) == false) {
                    LOGGER.warn("importLoaners: Skipping importing {} loaner as its not unique", l.getSchoolId());
                }

                else {
                    this.loanerRepository.save(l);
                    countImported++;
                }
            }

            csvParser.close();

            String message = String.format("%d loaners imported successfully", countImported);
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));

        }

        catch (FileNotFoundException e) {
            String message = "Error in retrieving uploaded file";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            String message = "React Coding issue: Incorrect mapping provided";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (IOException e) {
            String message = "Error in parsing uploaded file";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (NullPointerException e) {
            String message = "Coding issue: All nulls are not handled";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
        }

        catch (Exception e) {
            String message = "Something unexpected happened";
            LOGGER.error(message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, message));
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

        LOGGER.info("updateLoaner: loaner = {}, loanerId = {}", l, loanerId);

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

        LOGGER.info("deleteLoaner: loanerId = {}", loanerId);

        if (!this.loanerRepository.existsById(loanerId)) {
            String message = "Loaner not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(HttpStatus.NOT_FOUND, message));
        }

        else {
            Loaner toBeDeleted = this.loanerRepository.getById(loanerId);

            if (toBeDeleted.getTotalOutstanding() != 0) {
                String message = "Cannot delete loaner with outstanding books";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorBody(HttpStatus.BAD_REQUEST, message));
            }

            // reset all the transactions
            else {
                List<Transaction> trnsToBeChange = toBeDeleted.getTransactionHistory();

                for (Transaction trn : trnsToBeChange) {
                    trn.setLoaner(null);
                    this.transactionRepository.save(trn);
                }
            }

            this.loanerRepository.deleteById(loanerId);
            String message = "Loaner deleted successfully";
            return ResponseEntity.status(HttpStatus.OK).body(new ErrorBody(HttpStatus.OK, message));
        }
    }
}
