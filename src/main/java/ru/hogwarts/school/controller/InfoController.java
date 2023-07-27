package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.StudentService;

@RestController
public class InfoController {
    private final StudentService studentService;
    private final int port;

    public InfoController(StudentService studentService, @Value("${server.port}")int port) {
        this.studentService = studentService;
        this.port = port;
    }

    @GetMapping("/getPort")
    public int getPort() {
        return port;
    }

    @GetMapping("/getSum")
    public int getByParallel() {
        return studentService.getByParallel();
    }
}
