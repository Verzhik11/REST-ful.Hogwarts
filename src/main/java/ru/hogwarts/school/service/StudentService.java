package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
@Service
public class StudentService {
    private final HashMap<Long, Student> students;
    private Long positionId;

    public StudentService() {
        this.students = new HashMap<>();
        this.positionId = 1L;
    }

    public Student createStudent(Student Student) {
        students.put(positionId, Student);
        positionId++;
        return Student;
    }

    public Student getStudentById(long id) {
        return students.get(id);
    }

    public Student updateStudent(Long id, Student Student) {
        students.put(positionId, Student);
        return Student;
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }
}
