package com.budlib.api;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studentrepo")
@CrossOrigin
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping(value = "/students/{id}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Student> getStudentById(@PathVariable long id) {
        Student p = studentRepository.findById(id).get();
        if (p == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok().body(p);
    }

    @GetMapping(value = "/students", produces = { "application/json", "application/xml" })
    public ResponseEntity<Collection<Student>> getBooks() {
        Collection<Student> students = studentRepository.findAll().stream()
                .collect(Collectors.toList());
        System.out.println("Number of Students: " + students.size());
        return ResponseEntity.ok().body(students);
    }

    @PostMapping(value = "/addstudents", consumes = { "application/json", "application/xml" }, produces = {
            "application/json", "application/xml" })
    public ResponseEntity<?> insertProduct(@RequestBody Student student) {

        student.setId(studentRepository.count() + 1);
        studentRepository.save(student);
        URI uri = URI.create("/studentrepo/students/" + student.getId());
        return ResponseEntity.created(uri).body(student);
    }

    @PutMapping(value = "/updatestudents/{id}", consumes = { "application/json", "application/xml" })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @RequestBody Student student) {
        if (!studentRepository.existsById(id))
            return ResponseEntity.notFound().build();
        else
            studentRepository.save(student);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        Student student = studentRepository.findById(id).get();
        if (student == null)
            return ResponseEntity.notFound().build();
        else {
            studentRepository.delete(student);
            return ResponseEntity.ok().build();
        }

    }

}
