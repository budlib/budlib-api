package com.budlib.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, Long> {
    List<Student> findBystudentId(String studentId);

    List<Student> findByteacherName(String teacherName);

    List<Student> findByclassCode(String classCode);

}
