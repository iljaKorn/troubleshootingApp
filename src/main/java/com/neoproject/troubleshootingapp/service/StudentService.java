package com.neoproject.troubleshootingapp.service;

import com.neoproject.troubleshootingapp.exception.NotFoundException;
import com.neoproject.troubleshootingapp.model.Student;
import com.neoproject.troubleshootingapp.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> findAll() {
        log.trace("Вход в метод findAll");
        List<Student> students = studentRepository.findAll();
        log.info("Найдено {} студентов", students.size());

        log.trace("Выход из метода findAll");
        return students;
    }

    public Student createStudent(Student student) {
        log.trace("Вход в метод createStudent с параметром student: {}", student);

        if (student.getUniversity() == null){
            log.error("Попытка создать студента без университета: {}", student);
            throw new IllegalArgumentException("Университет обязателен для заполнения");
        }

        log.debug("Запушен цикл для имитации задержки");
        int num = 1;
        for (int i = 0; i < 1000000; i++) {
            num *= 1001;
            for (int j = 0; j < 1000; j++) {
                num -= 1;
            }
        }

        Student saveStudent = studentRepository.save(student);
        log.info("Студент с id: {} успешно сохранен", saveStudent.getId());

        log.trace("Выход из метода createStudent");
        return saveStudent;
    }

    public Student findById(Long id) {
        log.trace("Вход в метод findById с параметром id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Студент с id: {} не найден в методе findById", id);
                    return new NotFoundException(String.format("Студент с id: %d не найден", id));
                });
        log.info("Студент с id: {} успешно найден", student.getId());

        log.trace("Выход из метода findById");
        return student;
    }

    public Student updateStudent(Long id, Student student) {
        log.trace("Вход в метод updateStudent с параметрами id: {} и student: {}", id, student);
        Student studentFromDB = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Студент с id: {} не найден в методе updateStudent", id);
                    return new NotFoundException(String.format("Студент с id: %d не найден", id));
                });
        log.debug("Студент с id: {} успешно найден", studentFromDB.getId());


        studentFromDB.setFirstName(student.getFirstName());
        studentFromDB.setLastName(student.getLastName());
        studentFromDB.setUniversity(student.getUniversity());

        Student saveStudent = studentRepository.save(studentFromDB);
        log.info("Студент с id: {} успешно изменён", studentFromDB.getId());

        log.trace("Выход из метода updateStudent");
        return saveStudent;
    }

    public void deleteStudent(Long id) {
        log.trace("Вход в метод deleteStudent с параметром id: {}", id);
        studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Студент с id: {} не найден в методе deleteStudent", id);
                    return new NotFoundException(String.format("Студент с id: %d не найден", id));
                });

        studentRepository.deleteById(id);
        log.info("Студент с id: {} успешно удален", id);

        log.trace("Выход из метода deleteStudent");
    }
}
