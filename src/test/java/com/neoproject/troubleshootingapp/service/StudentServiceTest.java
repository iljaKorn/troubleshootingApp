package com.neoproject.troubleshootingapp.service;

import com.neoproject.troubleshootingapp.exception.NotFoundException;
import com.neoproject.troubleshootingapp.model.Student;
import com.neoproject.troubleshootingapp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("Иван");
        student1.setLastName("Иванов");
        student1.setUniversity("МГУ");

        student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Мария");
        student2.setLastName("Петрова");
        student2.setUniversity("СПбГУ");
    }

    @Test
    @DisplayName("findAll должен возвращать список всех студентов")
    void ShouldReturnAllStudents() {
        // Подготовка
        List<Student> expectedStudents = Arrays.asList(student1, student2);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // Действие
        List<Student> actualStudents = studentService.findAll();

        // Проверка
        assertNotNull(actualStudents);
        assertEquals(2, actualStudents.size());
        assertEquals("Иван", actualStudents.get(0).getFirstName());
        assertEquals("Мария", actualStudents.get(1).getFirstName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createStudent должен сохранять студента")
    void ShouldSaveStudent() {
        // Подготовка
        Student newStudent = new Student();
        newStudent.setFirstName("Петр");
        newStudent.setLastName("Сидоров");
        newStudent.setUniversity("КФУ");

        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        // Действие
        Student savedStudent = studentService.createStudent(newStudent);

        // Проверка
        assertNotNull(savedStudent);
        assertEquals("Петр", savedStudent.getFirstName());
        assertEquals("Сидоров", savedStudent.getLastName());
        assertEquals("КФУ", savedStudent.getUniversity());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("findById должен возвращать студента по ID")
    void ShouldReturnStudentById() {
        // Подготовка
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // Действие
        Student foundStudent = studentService.findById(1L);

        // Проверка
        assertNotNull(foundStudent);
        assertEquals(1L, foundStudent.getId());
        assertEquals("Иван", foundStudent.getFirstName());
        assertEquals("Иванов", foundStudent.getLastName());
        assertEquals("МГУ", foundStudent.getUniversity());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById должен выбрасывать NotFoundException, если студент не найден")
    void ShouldThrowNotFoundExceptionWhenStudentNotFoundForFindByIdMethod() {
        // Подготовка
        Long nonExistentId = 999L;
        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> studentService.findById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Студент с id: 999 не найден");
        verify(studentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("updateStudent должен обновлять существующего студента")
    void ShouldUpdateExistingStudent() {
        // Подготовка
        Long studentId = 1L;
        Student updatedInfo = new Student();
        updatedInfo.setFirstName("ИванUpdated");
        updatedInfo.setLastName("ИвановUpdated");
        updatedInfo.setUniversity("МГУUpdated");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student1));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // Действие
        Student updatedStudent = studentService.updateStudent(studentId, updatedInfo);

        // Проверка
        assertNotNull(updatedStudent);
        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).save(any(Student.class));

        assertEquals("ИванUpdated", student1.getFirstName());
        assertEquals("ИвановUpdated", student1.getLastName());
        assertEquals("МГУUpdated", student1.getUniversity());
    }

    @Test
    @DisplayName("updateStudent должен выбрасывать NotFoundException для несуществующего студента")
    void ShouldThrowNotFoundExceptionWhenStudentNotFoundForUpdateMethod() {
        // Подготовка
        Long nonExistentId = 999L;
        Student updatedInfo = new Student();
        updatedInfo.setFirstName("Новый");
        updatedInfo.setLastName("Студент");
        updatedInfo.setUniversity("Университет");

        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> studentService.findById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Студент с id: 999 не найден");

        verify(studentRepository, times(1)).findById(nonExistentId);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("deleteStudent должен удалять студента по ID")
    void deleteStudent_ShouldDeleteStudent() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student1));
        doNothing().when(studentRepository).deleteById(studentId);

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    @DisplayName("deleteStudent должен выбрасывать NotFoundException для несуществующего студента")
    void deleteStudent_ShouldThrowException_WhenStudentNotFound() {
        // Подготовка
        Long nonExistentId = 999L;
        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> studentService.deleteStudent(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Студент с id: 999 не найден");

        verify(studentRepository, times(1)).findById(nonExistentId);
        verify(studentRepository, never()).deleteById(anyLong());
    }
}