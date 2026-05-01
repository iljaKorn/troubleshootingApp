package com.neoproject.troubleshootingapp.service;

import com.neoproject.troubleshootingapp.exception.NotFoundException;
import com.neoproject.troubleshootingapp.model.Student;
import com.neoproject.troubleshootingapp.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student createStudent(Student student) {

        int num = 1;
        for(int i = 0; i < 1000000; i++){
            num *= 1001;
            for(int j = 0; j < 1000; j++){
                num -= 1;
            }
        }

        return studentRepository.save(student);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Студент с id: %d не найден", id)));
    }

    public Student updateStudent(Long id, Student student) {
        Student studentFromDB = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Студент с id: %d не найден", id)));

        studentFromDB.setFirstName(student.getFirstName());
        studentFromDB.setLastName(student.getLastName());
        studentFromDB.setUniversity(student.getUniversity());

        return studentRepository.save(studentFromDB);
    }

    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Студент с id: %d не найден", id)));

        studentRepository.deleteById(id);
    }
}
