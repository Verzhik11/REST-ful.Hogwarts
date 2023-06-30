package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collections;
import java.util.List;
@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;


    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public FacultyDtoOut createFaculty(@RequestBody FacultyDtoIn facultyDtoIn) {
        return facultyService.createFaculty(facultyDtoIn);
    }

    @GetMapping("/{id}")
    public FacultyDtoOut getFaculty(@PathVariable long id) {
        return facultyService.getFacultyById(id);
    }

    @PutMapping("/{id}")
    public FacultyDtoOut updateFaculty(@PathVariable long id, @RequestBody FacultyDtoIn facultyDtoIn) {
        return facultyService.updateFaculty(id, facultyDtoIn);

    }

    @DeleteMapping("/{id}")
    public FacultyDtoOut deleteFaculty(@PathVariable long id) {
        return facultyService.deleteFaculty(id);

    }
    @GetMapping("/color/{color}")
    public List<FacultyDtoOut> getFacultyByColor(@PathVariable String color) {
        return facultyService.getFacultyByColor(color);
    }
    @GetMapping("/colororname/{request}")
    public List<FacultyDtoOut> getFacultyByColorOrName(@PathVariable String request) {
        if (request != null && !request.isBlank()) {
            return facultyService.findByColorOrNameIgnoreCase(request);
        }
        return Collections.emptyList();
    }
    @GetMapping("/{id}/students")
    public List<StudentDtoOut> getStudentsByFaculty(@PathVariable long id) {
        return facultyService.getStudentsByFaculty(id);
    }


}
