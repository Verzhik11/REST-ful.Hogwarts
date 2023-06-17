package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.HashMap;
@Service
public class FacultyService {
    private final HashMap<Long, Faculty> faculties;
    private Long positionId;

    public FacultyService() {
        this.faculties = new HashMap<>();
        this.positionId = 1L;
    }

    public Faculty createFaculty(Faculty faculty) {
        faculties.put(positionId, faculty);
        positionId++;
        return faculty;
    }

    public Faculty getFacultyById(long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        faculties.put(positionId, faculty);
        return faculty;
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }
}
