package ru.hogwarts.school.controller;

import liquibase.pro.packaged.S;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public StudentDtoOut createStudent(@RequestBody StudentDtoIn studentDtoIn) {
        return studentService.createStudent(studentDtoIn);
    }

    @GetMapping("/{id}")
    public StudentDtoOut getStudent(@PathVariable long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public StudentDtoOut updateStudent(@PathVariable long id, @RequestBody StudentDtoIn studentDtoIn) {
        return studentService.updateStudent(id, studentDtoIn);
    }

    @DeleteMapping("/{id}")
    public StudentDtoOut deleteStudent(@PathVariable long id) {
        return studentService.deleteStudent(id);
    }

    @GetMapping
    public List<StudentDtoOut> getStudentByAge(@RequestParam(required = false) Integer age) {
        return studentService.getStudentByAge(age);
    }

    @GetMapping("/between/{min}/{max}")
    public List<StudentDtoOut> findStudentByAgeBetween(@PathVariable int min, @PathVariable int max) {
        if (min > 0 && max > 0 && max > min) {
            return studentService.findByAgeBetween(min, max);
        }
        return Collections.emptyList();
    }

    @GetMapping("/{id}/faculty")
    public FacultyDtoOut findFaculty(@PathVariable long id) {
        return studentService.getFacultyByStudent(id);
    }

    @PatchMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentDtoOut uploadAvatar(@PathVariable long id,
                                      @RequestPart("avatar") MultipartFile multipartFile) {
        return studentService.uploadAvatar(id, multipartFile);
    }

    @GetMapping("/getCount")
    public Integer getCount() {
        return studentService.getCount();
    }

    @GetMapping("/getAverageAge")
    public Integer getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/getLastFiveStudent")
    public List<StudentDtoOut> getLastFiveStudent() {
        return studentService.getLastFiveStudent();
    }

    @GetMapping("/getBySymbol/{symbol}")
    public List<StudentDtoOut> getStartWithSymbolStudents(@PathVariable String symbol) {
        return studentService.getStartWithSymbolStudents(symbol);
    }
    @GetMapping("/getAverageAgeByFindAll")
    public double getAverageAgeByFindAll() {
        return studentService.getAverageAgeByFindAll();

    }

    @GetMapping("/getNamesInStreams")
    public void getNamesInStreams() {
        studentService.getNamesInStreams();

    }
    @GetMapping("/getNamesInStreamsSynchronized")
    public void getNamesInStreamsSynchronized() {
        studentService.getNamesInStreamsSynchronized();

    }
}
