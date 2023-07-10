package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = FacultyController.class)
class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private FacultyService facultyService;

    @SpyBean
    private FacultyMapper facultyMapper;

    @SpyBean
    private StudentMapper studentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faker faker = new Faker();

    @Test
    public void createTest() throws Exception {
        FacultyDtoIn facultyDtoIn = generateDto();
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(facultyDtoIn.getName());
        faculty.setColor(facultyDtoIn.getColor());
        when(facultyRepository.save(any())).thenReturn(faculty);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/faculty")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(facultyDtoIn))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getColor()).isEqualTo(facultyDtoIn.getColor());
                    assertThat(facultyDtoOut.getName()).isEqualTo(facultyDtoIn.getName());
                });
        verify(facultyRepository, new Times(1)).save(any());
    }

    @Test
    public void updateTest() throws Exception {
        FacultyDtoIn facultyDtoIn = generateDto();

        Faculty oldFaculty = generate(1);

        when(facultyRepository.findById(eq(1L))).thenReturn(Optional.of(oldFaculty));

        oldFaculty.setColor(facultyDtoIn.getColor());
        oldFaculty.setName(facultyDtoIn.getName());
        when(facultyRepository.save(any())).thenReturn(oldFaculty);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/faculty/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(facultyDtoIn))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getColor()).isEqualTo(facultyDtoIn.getColor());
                    assertThat(facultyDtoOut.getName()).isEqualTo(facultyDtoIn.getName());
                });
        verify(facultyRepository, Mockito.times(1)).save(any());
        Mockito.reset(facultyRepository);

        // not found checking

        when(facultyRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/faculty/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(facultyDtoIn))
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Факультет с id = 2 не найден!");
                });
        verify(facultyRepository, never()).save(any());
    }

    @Test
    public void getTest() throws Exception {
        Faculty faculty = generate(1);

        when(facultyRepository.findById(eq(1L))).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyDtoOut.getName()).isEqualTo(faculty.getName());
                });

        // not found checking

        when(facultyRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/2")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Факультет с id = 2 не найден!");
                });
    }

    @Test
    public void deleteTest() throws Exception {
        Faculty faculty = generate(1);

        when(facultyRepository.findById(eq(1L))).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/faculty/1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    FacultyDtoOut facultyDtoOut = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            FacultyDtoOut.class
                    );
                    assertThat(facultyDtoOut).isNotNull();
                    assertThat(facultyDtoOut.getId()).isEqualTo(1L);
                    assertThat(facultyDtoOut.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyDtoOut.getName()).isEqualTo(faculty.getName());
                });
        verify(facultyRepository, times(1)).delete(any());
        Mockito.reset(facultyRepository);

        // not found checking

        when(facultyRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/faculty/2")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                    assertThat(responseString).isEqualTo("Факультет с id = 2 не найден!");
                });
        verify(facultyRepository, never()).delete(any());
    }

    @Test
    public void getFacultyByColorTest() throws Exception {
        List<Faculty> faculties = Stream.iterate(1, id -> id + 1)
                .map(this::generate)
                .limit(20)
                .collect(Collectors.toList());
        List<FacultyDtoOut> expectedResult = faculties.stream()
                .map(facultyMapper::tDto)
                .collect(Collectors.toList());

        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts)
                            .isNotNull()
                            .isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyDtoOuts.size())
                            .forEach(index -> {
                                FacultyDtoOut facultyDtoOut = facultyDtoOuts.get(index);
                                FacultyDtoOut expected = expectedResult.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });

        String color = faculties.get(0).getColor();
        faculties = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .collect(Collectors.toList());
        List<FacultyDtoOut> expectedResult2 = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .map(facultyMapper:: tDto)
                .collect(Collectors.toList());
        when(facultyRepository.findByColor(eq(color))).thenReturn(faculties);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty?color={color}", color)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts)
                            .isNotNull()
                            .isNotEmpty();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyDtoOuts.size())
                            .forEach(index -> {
                                FacultyDtoOut facultyDtoOut = facultyDtoOuts.get(index);
                                FacultyDtoOut expected = expectedResult2.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });
    }
    @Test
    public void findByColorOrNameIgnoreCaseTest() throws Exception {
        List<Faculty> faculties = Stream.iterate(1, id -> id + 1)
                .map(this::generate)
                .limit(2)
                .collect(Collectors.toList());
        faculties.get(0).setColor("GrEEn");
        faculties.get(0).setName("AndY");
        faculties.get(1).setColor("GreEn");
        faculties.get(1).setName("Elena");
        String color = "green";

        when(facultyRepository.findByColorOrNameIgnoreCase("green", "green"))
                .thenReturn(List.of(new Faculty(1L, "AndY", "GrEEn"),
                        new Faculty(2L, "Elena", "GrEEn")));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/colororname/green")
                ).andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(facultyService.findByColorOrNameIgnoreCase("green")).hasSize(2);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/colororname/null")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<FacultyDtoOut> facultyDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(facultyDtoOuts)
                            .isNotNull()
                            .isEmpty();
                });
    }
    @Test
    public void getStudentsByFacultyTest() throws Exception {
       List<Student> students = List.of(new Student(1L, "Alex", 16),
               new Student(2L, "Susana", 17),
               new Student(3L, "Ivan", 18),
               new Student(4L, "Sveta", 16));
       Faculty faculty1 = new Faculty(2L, "Математика", "зеленый");
        Faculty faculty2 = new Faculty(3L, "Физика", "синий");
        students.get(0).setFaculty(faculty1);
        students.get(1).setFaculty(faculty1);
        students.get(2).setFaculty(faculty2);
        students.get(3).setFaculty(faculty2);

       long faculty_id = students.get(1).getFaculty().getId();
        List<StudentDtoOut> expectedResult = students.stream()
                .filter(student -> student.getFaculty().getId() == (faculty_id))
                .map(studentMapper:: tDto)
                .collect(Collectors.toList());
        when(studentRepository.findAllByFaculty_Id(eq(faculty_id))).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/3/students")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<StudentDtoOut> studentDtoOuts = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            }
                    );
                    assertThat(studentDtoOuts)
                            .isNotNull();
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentDtoOuts.size())
                            .forEach(index -> {
                                StudentDtoOut studentDtoOut = studentDtoOuts.get(index);
                                StudentDtoOut expected = expectedResult.get(index);
                                assertThat(studentDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(studentDtoOut.getAge()).isEqualTo(expected.getAge());
                                assertThat(studentDtoOut.getName()).isEqualTo(expected.getName());
                                assertThat(studentDtoOut.getFaculty().getId()).isEqualTo(expected.getFaculty().getId());
                            });
                });



    }

    private FacultyDtoIn generateDto() {
        FacultyDtoIn facultyDtoIn = new FacultyDtoIn();
        facultyDtoIn.setName(faker.harryPotter().house());
        facultyDtoIn.setColor(faker.color().name());
        return facultyDtoIn;
    }

    private Faculty generate(long id) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return faculty;
    }

}