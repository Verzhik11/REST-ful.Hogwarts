package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.dto.*;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AvatarRepository avatarRepository;

    @SpyBean
    private StudentService studentService;
    @SpyBean
    private AvatarService avatarService;

    @SpyBean
    private StudentMapper studentMapper;

    @SpyBean
    private FacultyMapper facultyMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faker faker = new Faker();

    @Test
    public void createStudentTest() throws Exception {
        FacultyDtoIn facultyDtoIn = generateFacultyDto();
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(facultyDtoIn.getName());
        faculty.setColor(facultyDtoIn.getColor());
        when(facultyRepository.save(ArgumentMatchers.any())).thenReturn(faculty);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        StudentDtoIn studentDtoIn = generateStudentDto();
        studentDtoIn.setFaculty_id(faculty.getId());
        Student student = new Student();
        student.setId(1L);
        student.setName(studentDtoIn.getName());
        student.setAge(studentDtoIn.getAge());
        when(studentRepository.save(ArgumentMatchers.any())).thenReturn(student);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/student")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studentDtoIn))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
                    assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());
                });
        verify(studentRepository, new Times(1)).save(ArgumentMatchers.any());
    }
    @Test
    public void updateStudentTest() throws Exception {
        StudentDtoIn studentDtoIn = generateStudentDto();

        Student oldStudent = generate(1);

        when(studentRepository.findById(eq(1L))).thenReturn(Optional.of(oldStudent));

        FacultyDtoIn facultyDtoIn = generateFacultyDto();
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(facultyDtoIn.getName());
        faculty.setColor(facultyDtoIn.getColor());
        studentDtoIn.setFaculty_id(faculty.getId());
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        oldStudent.setAge(studentDtoIn.getAge());
        oldStudent.setName(studentDtoIn.getName());
        when(studentRepository.save(ArgumentMatchers.any())).thenReturn(oldStudent);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/student/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studentDtoIn))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getAge()).isEqualTo(studentDtoIn.getAge());
                    assertThat(studentDtoOut.getName()).isEqualTo(studentDtoIn.getName());
                });
        verify(studentRepository, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.reset(studentRepository);

        // not found checking

        when(studentRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/student/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studentDtoIn))
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Студент с id = 2 не найден!");
                });
        verify(studentRepository, never()).save(ArgumentMatchers.any());
    }
    @Test
    public void getStudentTest() throws Exception {
        Student student = generate(1);

        when(studentRepository.findById(eq(1L))).thenReturn(Optional.of(student));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student/1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getAge()).isEqualTo(student.getAge());
                    assertThat(studentDtoOut.getName()).isEqualTo(student.getName());
                });

        // not found checking

        when(studentRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student/2")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Студент с id = 2 не найден!");
                });
    }
    @Test
    public void deleteTest() throws Exception {
        Student student = generate(1);

        when(studentRepository.findById(eq(1L))).thenReturn(Optional.of(student));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/student/1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    StudentDtoOut studentDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            StudentDtoOut.class
                    );
                    assertThat(studentDtoOut).isNotNull();
                    assertThat(studentDtoOut.getId()).isEqualTo(1L);
                    assertThat(studentDtoOut.getAge()).isEqualTo(student.getAge());
                    assertThat(studentDtoOut.getName()).isEqualTo(student.getName());
                });
        verify(studentRepository, times(1)).delete(ArgumentMatchers.any());
        Mockito.reset(studentRepository);

        // not found checking

        when(studentRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/student/2")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Студент с id = 2 не найден!");
                });
        verify(studentRepository, never()).delete(ArgumentMatchers.any());
    }
    @Test
    public void getStudentByAgeTest() throws Exception {
        List<Student> students = Stream.iterate(1, id -> id + 1)
                .map(this::generate)
                .limit(10)
                .collect(Collectors.toList());
        List<StudentDtoOut> expectedResult = students.stream()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts)
                            .isNotNull()
                            .isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut studentDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult.get(index);
                                assertThat(studentDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(studentDtoOut.getAge()).isEqualTo(expected.getAge());
                                assertThat(studentDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });

        Integer age = students.get(0).getAge();
        students = students.stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
        List<StudentDtoOut> expectedResult2 = students.stream()
                .filter(student -> student.getAge() == age)
                .map(studentMapper:: tDto)
                .collect(Collectors.toList());
        when(studentRepository.findByAge(eq(age))).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student/?age={age}", age)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts)
                            .isNotNull()
                            .isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut facultyDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult2.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getAge()).isEqualTo(expected.getAge());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });
    }
    @Test
    public void getStudentBetweenAgeTest() throws Exception {
        List<Student> students = Stream.iterate(1, id -> id + 1)
                .map(this::generate)
                .limit(10)
                .filter(student -> student.getAge() > 10 && student.getAge() < 14)
                .collect(Collectors.toList());
        List<StudentDtoOut> expectedResult = students.stream()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());
        when(studentRepository.findByAgeBetween(10, 14)).thenReturn(students);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student/between/10/14")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts)
                            .isNotNull()
                            .isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut facultyDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getAge()).isEqualTo(expected.getAge());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });

    }

    @Test
    public void findFacultyTest() throws Exception{
        Student student = generate(1);
        Faculty faculty = generateFaculty(1);
        student.setFaculty(faculty);

        when(studentRepository.findById(eq(1L))).thenReturn(Optional.of(student));
        when(facultyRepository.findById(eq(1L))).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/student/1/faculty")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getName()).isEqualTo(student.getFaculty().getName());
                    assertThat(facultyDtoOut.getColor()).isEqualTo(student.getFaculty().getColor());
                });
    }


    public StudentDtoIn generateStudentDto() {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setAge(faker.random().nextInt(7, 18));
        studentDtoIn.setName(faker.name().fullName());
        return studentDtoIn;
    }
    private FacultyDtoIn generateFacultyDto() {
        FacultyDtoIn facultyDtoIn = new FacultyDtoIn();
        facultyDtoIn.setName(faker.harryPotter().house());
        facultyDtoIn.setColor(faker.color().name());
        return facultyDtoIn;
    }
    private Student generate(long id) {
        Student student = new Student();
        student.setId(id);
        student.setName(faker.name().fullName());
        student.setAge(faker.random().nextInt(7, 18));
        return student;
    }
   private Faculty generateFaculty(long id) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return faculty;
    }


}