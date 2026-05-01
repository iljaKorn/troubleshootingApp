package com.neoproject.troubleshootingapp.repository;

import com.neoproject.troubleshootingapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
