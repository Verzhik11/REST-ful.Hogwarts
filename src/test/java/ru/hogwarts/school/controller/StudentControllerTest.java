package ru.hogwarts.school.controller;

import com.github.javafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.FacultyDtoOut;
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
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentController studentController;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void contextLoad() throws Exception {
        Assertions.assertThat(studentController).isNotNull();

    }

    @BeforeEach
    public void add() {
        StudentDtoOut studentDtoOut = new StudentDtoOut();
        StudentDtoOut studentDtoOut1 = new StudentDtoOut();
        studentDtoOut.setName("Alan Po");
        studentDtoOut.setAge(18);
        studentDtoOut1.setName("Elen Ar");
        studentDtoOut1.setAge(19);
    }



    @AfterEach
    public void clean() {
        studentRepository.deleteAll();
    }


        @Test
        public void testCreateStudent() {
            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("John Doe");
            studentDtoIn.setAge(20);

            ResponseEntity<StudentDtoOut> response = restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoOut.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            StudentDtoOut createdStudent = response.getBody();
            assertNotNull(createdStudent);
            assertNotNull(createdStudent.getId());
            assertEquals("John Doe", createdStudent.getName());
            assertEquals(20, createdStudent.getAge());
        }

        @Test
        public void testGetStudent() {
            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("Jane Smith");
            studentDtoIn.setAge(22);
            ResponseEntity<StudentDtoOut> createResponse = restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoOut.class);
            StudentDtoOut createdStudent = createResponse.getBody();

            ResponseEntity<StudentDtoOut> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/student/{id}", StudentDtoOut.class, createdStudent.getId());

            assertEquals(HttpStatus.OK, getResponse.getStatusCode());
            StudentDtoOut retrievedStudent = getResponse.getBody();
            assertNotNull(retrievedStudent);
            assertEquals(createdStudent.getId(), retrievedStudent.getId());
            assertEquals("Jane Smith", retrievedStudent.getName());
            assertEquals(22, retrievedStudent.getAge());
        }

        @Test
        public void testUpdateStudent() {
            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("Tom White");
            studentDtoIn.setAge(25);
            ResponseEntity<StudentDtoOut> createResponse = restTemplate.postForEntity("http://localhost:" + port + "/students", studentDtoIn, StudentDtoOut.class);
            StudentDtoOut createdStudent = createResponse.getBody();

            StudentDtoIn updatedStudentDtoIn = new StudentDtoIn();
            updatedStudentDtoIn.setName("Tom Green");
            updatedStudentDtoIn.setAge(26);
            ResponseEntity<StudentDtoOut> updateResponse = restTemplate.exchange("/student/{id}", HttpMethod.PUT, new HttpEntity<>(updatedStudentDtoIn), StudentDtoOut.class, createdStudent.getId());

            assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
            StudentDtoOut updatedStudent = updateResponse.getBody();
            assertNotNull(updatedStudent);
            assertEquals(createdStudent.getId(), updatedStudent.getId());
            assertEquals("Tom Green", updatedStudent.getName());
            assertEquals(26, updatedStudent.getAge());
        }

        @Test
        public void testDeleteStudent() {

            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("Alice Brown");
            studentDtoIn.setAge(23);
            ResponseEntity<StudentDtoOut> createResponse = restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoOut.class);
            StudentDtoOut createdStudent = createResponse.getBody();


            ResponseEntity<StudentDtoOut> deleteResponse = restTemplate.exchange("http://localhost:" + port + "/student/{id}", HttpMethod.DELETE, null, StudentDtoOut.class, createdStudent.getId());


            assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
            StudentDtoOut deletedStudent = deleteResponse.getBody();
            assertNotNull(deletedStudent);
            assertEquals(createdStudent.getId(), deletedStudent.getId());
            assertEquals("Alice Brown", deletedStudent.getName());
            assertEquals(23, deletedStudent.getAge());
        }

        @Test
        public void testGetStudentByAge() {

            StudentDtoIn studentDtoIn1 = new StudentDtoIn();
            studentDtoIn1.setName("John Doe");
            studentDtoIn1.setAge(20);
            restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn1, StudentDtoOut.class);

            StudentDtoIn studentDtoIn2 = new StudentDtoIn();
            studentDtoIn2.setName("Jane Smith");
            studentDtoIn2.setAge(22);
            restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn2, StudentDtoOut.class);


            ResponseEntity<List<StudentDtoOut>> response = restTemplate.exchange("http://localhost:" + port + "/student/age/{age}", HttpMethod.GET, null, new ParameterizedTypeReference<List<StudentDtoOut>>() {}, 20);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<StudentDtoOut> studentsByAge = response.getBody();
            assertNotNull(studentsByAge);
            assertEquals(1, studentsByAge.size());

            StudentDtoOut student = studentsByAge.get(0);
            assertEquals("John Doe", student.getName());
            assertEquals(20, student.getAge());
        }

        @Test
        public void testFindStudentByAgeBetween() {

            StudentDtoIn studentDtoIn1 = new StudentDtoIn();
            studentDtoIn1.setName("John Doe");
            studentDtoIn1.setAge(20);
            restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn1, StudentDtoOut.class);

            StudentDtoIn studentDtoIn2 = new StudentDtoIn();
            studentDtoIn2.setName("Jane Smith");
            studentDtoIn2.setAge(22);
            restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn2, StudentDtoOut.class);


            ResponseEntity<List<StudentDtoOut>> response = restTemplate.exchange("http://localhost:" + port + "/student/between/{min}/{max}", HttpMethod.GET, null, new ParameterizedTypeReference<List<StudentDtoOut>>() {}, 18, 25);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<StudentDtoOut> studentsByAgeBetween = response.getBody();
            assertNotNull(studentsByAgeBetween);
            assertEquals(2, studentsByAgeBetween.size());

            StudentDtoOut student1 = studentsByAgeBetween.get(0);
            assertEquals("John Doe", student1.getName());
            assertEquals(20, student1.getAge());

            StudentDtoOut student2 = studentsByAgeBetween.get(1);
            assertEquals("Jane Smith", student2.getName());
            assertEquals(22, student2.getAge());
        }

        @Test
        public void testFindFaculty() {

            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("John Doe");
            studentDtoIn.setAge(20);
            ResponseEntity<StudentDtoOut> createResponse = restTemplate.postForEntity("http://localhost:" + port + "/student", studentDtoIn, StudentDtoOut.class);
            StudentDtoOut createdStudent = createResponse.getBody();


            ResponseEntity<FacultyDtoOut> response = restTemplate.getForEntity("http://localhost:" + port + "/student/{id}/faculty", FacultyDtoOut.class, createdStudent.getId());


            assertEquals(HttpStatus.OK, response.getStatusCode());
            FacultyDtoOut faculty = response.getBody();
            assertNotNull(faculty);

        }

        /*@Test
        public void testUploadAvatar() {
            // Предварительное создание студента для тестирования
            StudentDtoIn studentDtoIn = new StudentDtoIn();
            studentDtoIn.setName("John Doe");
            studentDtoIn.setAge(20);
            ResponseEntity<StudentDtoOut> createResponse = restTemplate.postForEntity("/student", studentDtoIn, StudentDtoOut.class);
            StudentDtoOut createdStudent = createResponse.getBody();

            // Создание MultipartFile с данными о файле
            ClassPathResource imageResource = new ClassPathResource("avatar.jpg");
            MultipartFile multipartFile;
            try {
                multipartFile = new MockMultipartFile("avatar", imageResource.getInputStream());
            } catch (IOException e) {
                // Обработка ошибки, если не удалось получить файл
                e.printStackTrace();
                return;
            }

            // Отправка PATCH-запроса на /student/{id}/avatar для загрузки аватара
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartFile, headers);
            ResponseEntity<StudentDtoOut> response = restTemplate.exchange("/student/{id}/avatar", HttpMethod.PATCH, requestEntity, StudentDtoOut.class, createdStudent.getId());

            // Проверка статуса ответа и получение студента с обновленным аватаром
            assertEquals(HttpStatus.OK, response.getStatusCode());
            StudentDtoOut updatedStudent = response.getBody();
            assertNotNull(updatedStudent);
            assertNotNull(updatedStudent.getAvatarUrl());
            // Проверка ожидаемого URL аватара
            // ...
        }*/

}