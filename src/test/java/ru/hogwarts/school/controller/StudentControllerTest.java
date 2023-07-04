package ru.hogwarts.school.controller;

import com.github.javafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentController studentController;
    private final Faker faker = new Faker();

    @Test
    public void contextLoad() throws Exception {
        Assertions.assertThat(studentController).isNotNull();

    }
    @Test
    public void getTest() throws Exception {
        Assertions.assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/students",
                StudentDtoOut.class)).isNotNull();
    }
    @Test
    public void postTest() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setAge(15);
        studentDtoIn.setName("dfdf");
        Assertions.assertThat(this.testRestTemplate.postForObject("http://localhost:" + port + "/students", studentDtoIn,
                StudentDtoOut.class)).isNotNull();
    }
    @Test
    public void updateTest() throws Exception {
        StudentDtoOut studentDtoOut = new StudentDtoOut();
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setName(faker.name().fullName());
        studentDtoIn.setAge(studentDtoOut.getAge());
        Assertions.assertThat(this.testRestTemplate.exchange("http://localhost:" + port + "/students"
                + studentDtoOut.getId(), HttpMethod.PUT,
                new HttpEntity<>(studentDtoIn),
                StudentDtoOut.class)).isNotNull();
    }


    public StudentDtoIn generate() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setAge(faker.random().nextInt(7, 18));
        studentDtoIn.setName(faker.name().fullName());
        return studentDtoIn;
    }

}