package com.budlib.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, Long> {
    Optional<Student> findById(Long id);

    List<Student> findByteacherName(String teacherName);

    List<Student> findByclassCode(String classCode);

}
