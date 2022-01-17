package com.budlib.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookDbGenerator {
    private ArrayList<Book> books;
    private ArrayList<Student> students;

    @Autowired
    BookRepository brepository;

    @Autowired
    StudentRepository srepository;

    @Autowired
    TransactionRepository tRepository;

    @Autowired
    LibrarianRepository lRepository;

    public void genStudents(int studentpop) {
        Random rand = new Random();

        ArrayList<String> classecodeArr = new ArrayList<String>() {
            {
                add("G1a");
                add("G1b");
                add("G2a");
                add("G2b");
                add("G3a");
                add("G3b");
                add("G4a");
                add("G4b");
                add("G5a");
                add("G5b");
                add("G6a");
                add("G6b");
            }
        };

        Faker fakegen = new Faker();
        for (int i = 0; i < studentpop; i++) {

            students.add(
                    new Student((long) i, fakegen.name().firstName(), fakegen.name().lastName(),
                            fakegen.name().fullName(), classecodeArr.get(rand.nextInt(classecodeArr.size()))));

        }

        System.out.println("Student suppose to add: " + studentpop + " Students added: " + this.students.size());

    }

    private void loadModel() {
        students = new ArrayList<Student>();
        books = new ArrayList<Book>();
        String line = "";
        String splitBy = ",";
        Book temp;
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("C:\\Downloads\\library_test.csv"));

            while ((line = br.readLine()) != null) // returns a boolean value
            {
                String[] read_line = line.split(splitBy); // use comma as separator
                if (Integer.parseInt(read_line[0]) != -1) {
                    temp = new Book(Long.parseLong(read_line[0]), read_line[1], read_line[2], read_line[3],
                            read_line[4], read_line[5], read_line[6]);
                    this.books.add(temp);
                }

            }

            br.close();

            System.out.println("Number of books added: " + this.books.size());

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() {
        this.loadModel();
        for (Book book : this.books) {
            brepository.save(book);
        }

        this.genStudents(100);
        for (Student student : this.students) {
            srepository.save(student);
        }

        Transaction transaction = new Transaction(Long.valueOf(1), Long.valueOf(1), Long.valueOf(1), "2022-01-01",
                "Return");
        tRepository.save(transaction);

        Librarian librarian = new Librarian(Long.valueOf(1), "thanos", "Leo", "Da Vinci", "admin123", "ADMIN");
        lRepository.save(librarian);

    }

    @PreDestroy
    public void cleanup() {
        brepository.deleteAll();
        srepository.deleteAll();
        tRepository.deleteAll();
        lRepository.deleteAll();
    }

}
