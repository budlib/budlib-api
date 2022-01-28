package com.budlib.api.repository;

import java.util.List;
import java.util.Optional;
import com.budlib.api.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, Long> {
    Optional<Student> findById(Long id);

    List<Student> findByteacherName(String teacherName);

    List<Student> findByclassCode(String classCode);
}
