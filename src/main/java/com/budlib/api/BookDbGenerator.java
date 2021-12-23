package com.budlib.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookDbGenerator {
    private ArrayList<Book> books;

    @Autowired
    BookRepository repository;

    private void loadModel() {
        books = new ArrayList<Book>();
        String line = "";
        String splitBy = ",";
        Book temp;
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("C:\\Downloads\\google_books_dataset.csv"));

            while ((line = br.readLine()) != null) // returns a boolean value
            {
                String[] read_line = line.split(splitBy); // use comma as separator
                if (Integer.parseInt(read_line[0]) != -1) {
                    temp = new Book(Long.parseLong(read_line[0]), read_line[1], read_line[2], read_line[3],
                            read_line[4], Double.parseDouble(read_line[5]), read_line[6], read_line[7], read_line[8],
                            Integer.parseInt(read_line[9]), "000");
                    this.books.add(temp);
                }

            }

            br.close();

            System.out.println("Number of books added: " + this.books.size());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() {
        this.loadModel();
        for (Book book : this.books) {
            repository.save(book);
        }

    }

    @PreDestroy
    public void cleanup() {
        repository.deleteAll();
    }

}
