package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jca.cci.object.SimpleRecordOperation;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);
    List<Student> findByAgeBetween(int min, int max);

    List<Student> findAllByFaculty_Id(long faculty_id);
    @Query(value = "SELECT count(*) FROM student", nativeQuery = true)
    Integer getCount();
    @Query(value = "select avg(age) from student", nativeQuery = true)
    Integer getAverageAge();
    @Query(value = "select * from student order by id offset 2", nativeQuery = true)
    List<Student> getLastFiveStudent();

}
