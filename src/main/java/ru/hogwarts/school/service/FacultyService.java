package ru.hogwarts.school.service;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDtoIn;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public FacultyService(FacultyRepository facultyRepository, FacultyMapper facultyMapper,
                          StudentRepository studentRepository, StudentMapper studentMapper) {
        this.facultyRepository = facultyRepository;
        this.facultyMapper = facultyMapper;
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }


    public FacultyDtoOut createFaculty(FacultyDtoIn facultyDtoIn) {
        return facultyMapper.tDto(facultyRepository.save(facultyMapper.toEntity(facultyDtoIn)));
    }

    public FacultyDtoOut getFacultyById(long id) {
        return facultyRepository.findById(id)
                .map(facultyMapper::tDto)
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public FacultyDtoOut updateFaculty(long id, FacultyDtoIn facultyDtoIn) {
        return facultyRepository.findById(id)
                .map(oldFaculty -> {
                    oldFaculty.setColor(facultyDtoIn.getColor());
                    oldFaculty.setName(facultyDtoIn.getName());
                    return facultyMapper.tDto(facultyRepository.save(oldFaculty));
                })
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public FacultyDtoOut deleteFaculty(long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));
        facultyRepository.delete(faculty);
        return facultyMapper.tDto(faculty);

    }

    public List<FacultyDtoOut> getFacultyByColor(@Nullable String color) {
        return Optional.ofNullable(color)
                .map(facultyRepository :: findByColor)
                .orElseGet(facultyRepository :: findAll).stream()
                .map(facultyMapper ::tDto)
                .collect(Collectors.toList());
    }

    public List<FacultyDtoOut> findByColorOrNameIgnoreCase(String colorOrName) {
        return facultyRepository.findByColorOrNameIgnoreCase(colorOrName, colorOrName).stream()
                .map(facultyMapper ::tDto)
                .collect(Collectors.toList());
    }

    public List<StudentDtoOut> getStudentsByFaculty(long faculty_id) {
        return studentRepository.findAllByFaculty_Id(faculty_id).stream()
                .map(studentMapper ::tDto)
                .collect(Collectors.toList());

    }

}
