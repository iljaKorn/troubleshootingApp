package com.neoproject.troubleshootingapp.controller;

import com.neoproject.troubleshootingapp.exception.NotFoundException;
import com.neoproject.troubleshootingapp.model.Student;
import com.neoproject.troubleshootingapp.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("Тесты StudentController")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private Student testStudent;
    private Student testStudent2;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setFirstName("Иван");
        testStudent.setLastName("Иванов");
        testStudent.setUniversity("МГУ");

        testStudent2 = new Student();
        testStudent2.setId(2L);
        testStudent2.setFirstName("Мария");
        testStudent2.setLastName("Петрова");
        testStudent2.setUniversity("СПбГУ");
    }

    @Test
    @DisplayName("GET /students/find/all - должен возвращать список всех студентов")
    void ShouldReturnAllStudents() throws Exception {
        // Подготовка
        List<Student> students = Arrays.asList(testStudent, testStudent2);
        when(studentService.findAll()).thenReturn(students);

        // Действие и проверка
        mockMvc.perform(get("/students/find/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Иван")))
                .andExpect(jsonPath("$[0].lastName", is("Иванов")))
                .andExpect(jsonPath("$[0].university", is("МГУ")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Мария")))
                .andExpect(jsonPath("$[1].lastName", is("Петрова")))
                .andExpect(jsonPath("$[1].university", is("СПбГУ")));

        verify(studentService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /students/find/{id} - должен возвращать студента по ID")
    void ShouldReturnStudentWhenExists() throws Exception {
        // Подготовка
        when(studentService.findById(1L)).thenReturn(testStudent);

        // Действие и проверка
        mockMvc.perform(get("/students/find/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Иван")))
                .andExpect(jsonPath("$.lastName", is("Иванов")))
                .andExpect(jsonPath("$.university", is("МГУ")));

        verify(studentService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /students/find/{id} - должен возвращать 404, если студент не найден")
    void ShouldReturn404WhenNotFoundForFindByIdMethod() throws Exception {
        // Подготовка
        Long nonExistentId = 999L;
        when(studentService.findById(nonExistentId))
                .thenThrow(new NotFoundException("Студент с id: 999 не найден"));

        // Действие и проверка
        mockMvc.perform(get("/students/find/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("POST /students/create - должен создавать нового студента")
    void ShouldCreateAndReturnStudent() throws Exception {
        // Подготовка
        Student newStudent = new Student();
        newStudent.setFirstName("Петр");
        newStudent.setLastName("Сидоров");
        newStudent.setUniversity("КФУ");

        Student savedStudent = new Student();
        savedStudent.setId(3L);
        savedStudent.setFirstName("Петр");
        savedStudent.setLastName("Сидоров");
        savedStudent.setUniversity("КФУ");

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        // Действие и проверка
        mockMvc.perform(post("/students/create")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.firstName", is("Петр")))
                .andExpect(jsonPath("$.lastName", is("Сидоров")))
                .andExpect(jsonPath("$.university", is("КФУ")));

        verify(studentService, times(1)).createStudent(any(Student.class));
    }

    @Test
    @DisplayName("PUT /students/update/{id} - должен обновлять существующего студента")
    void ShouldUpdateAndReturnStudent() throws Exception {
        // Подготовка
        Long updateId = 1L;
        Student updateInfo = new Student();
        updateInfo.setFirstName("ИванUpdated");
        updateInfo.setLastName("ИвановUpdated");
        updateInfo.setUniversity("МГУUpdated");

        Student updatedStudent = new Student();
        updatedStudent.setId(updateId);
        updatedStudent.setFirstName("ИванUpdated");
        updatedStudent.setLastName("ИвановUpdated");
        updatedStudent.setUniversity("МГУUpdated");

        when(studentService.updateStudent(eq(updateId), any(Student.class)))
                .thenReturn(updatedStudent);

        // Действие и проверка
        mockMvc.perform(put("/students/update/{id}", updateId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("ИванUpdated")))
                .andExpect(jsonPath("$.lastName", is("ИвановUpdated")))
                .andExpect(jsonPath("$.university", is("МГУUpdated")));

        verify(studentService, times(1)).updateStudent(eq(updateId), any(Student.class));
    }

    @Test
    @DisplayName("PUT /students/update/{id} - должен возвращать 404, если студент не найден")
    void ShouldReturn404WhenNotFoundForUpdateMethod() throws Exception {
        // Подготовка
        Long nonExistentId = 999L;
        Student updateInfo = new Student();
        updateInfo.setFirstName("Новый");
        updateInfo.setLastName("Студент");
        updateInfo.setUniversity("Университет");

        when(studentService.updateStudent(eq(nonExistentId), any(Student.class)))
                .thenThrow(new NotFoundException("Студент с id: 999 не найден"));

        // Действие и проверка
        mockMvc.perform(put("/students/update/{id}", nonExistentId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).updateStudent(eq(nonExistentId), any(Student.class));
    }

    @Test
    @DisplayName("DELETE /students/delete/{id} - должен успешно удалять существующего студента")
    void ShouldDeleteAndReturnOk() throws Exception {
        // Подготовка
        Long existingId = 1L;
        doNothing().when(studentService).deleteStudent(existingId);

        // Действие и проверка
        mockMvc.perform(delete("/students/delete/{id}", existingId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(existingId);
    }

    @Test
    @DisplayName("DELETE /students/delete/{id} - должен возвращать 404, если студент не найден")
    void ShouldReturn404WhenNotFoundForDeleteMethod() throws Exception {
        // Подготовка
        Long nonExistentId = 999L;
        doThrow(new NotFoundException("Студент с id: 999 не найден"))
                .when(studentService).deleteStudent(nonExistentId);

        // Действие и проверка
        mockMvc.perform(delete("/students/delete/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).deleteStudent(nonExistentId);
    }
}