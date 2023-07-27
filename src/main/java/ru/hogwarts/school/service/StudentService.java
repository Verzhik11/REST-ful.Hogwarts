package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDtoOut;
import ru.hogwarts.school.dto.StudentDtoIn;
import ru.hogwarts.school.dto.StudentDtoOut;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;
    private final AvatarService avatarService;
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private Integer count = 0;


    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper,
                          FacultyRepository facultyRepository, FacultyMapper facultyMapper, AvatarService avatarService) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.facultyRepository = facultyRepository;
        this.facultyMapper = facultyMapper;
        this.avatarService = avatarService;
    }

    public StudentDtoOut createStudent(StudentDtoIn studentDtoIn) {
        logger.info("Was invoked method for create student");
        return studentMapper.tDto(studentRepository.save(studentMapper.toEntity(studentDtoIn)));
    }

    public StudentDtoOut getStudentById(long id) {
        logger.info("Was invoked method for get with id = {}", id);
        return studentRepository.findById(id)
                .map(studentMapper::tDto)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public StudentDtoOut updateStudent(long id, StudentDtoIn studentDtoIn) {
        logger.info("Was invoked method for update with id = {}", id);
        return studentRepository.findById(id)
                .map(oldStudent -> {
                    oldStudent.setAge(studentDtoIn.getAge());
                    oldStudent.setName(studentDtoIn.getName());
                    Optional.ofNullable(studentDtoIn.getFaculty_id())
                            .ifPresent(faculty_id -> oldStudent.setFaculty(facultyRepository.findById(faculty_id)
                                    .orElseThrow(() -> new FacultyNotFoundException(faculty_id))));
                    return studentMapper.tDto(studentRepository.save(oldStudent));
                })
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public StudentDtoOut deleteStudent(long id) {
        logger.info("Was invoked method for delete with id = {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return studentMapper.tDto(student);

    }

    public List<StudentDtoOut> getStudentByAge(@Nullable Integer age) {
        logger.info("Was invoked method for getStudentByAge");
        return Optional.ofNullable(age)
                .map(studentRepository::findByAge)
                .orElseGet(studentRepository::findAll).stream()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());
    }

    public List<StudentDtoOut> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for findByAgeBetween");
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());
    }

    public FacultyDtoOut getFacultyByStudent(long id) {
        logger.info("Was invoked method for getFacultyByStudent");
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .map(facultyMapper::tDto)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public StudentDtoOut uploadAvatar(long id, MultipartFile multipartFile) {
        logger.info("Was invoked method for uploadAvatar");
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        avatarService.create(student, multipartFile);
        return studentMapper.tDto(student);
    }

    public Integer getCount() {
        logger.info("Was invoked method for getCount");
        return studentRepository.getCount();
    }

    public Integer getAverageAge() {
        logger.info("Was invoked method for getAverageAge");
        return studentRepository.getAverageAge();
    }

    @Transactional(readOnly = true)
    public List<StudentDtoOut> getLastFiveStudent() {
        logger.info("Was invoked method for getLastFiveStudent");
        return studentRepository.getLastFiveStudent().stream()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());
    }

    public List<StudentDtoOut> getStartWithSymbolStudents(String symbol) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getName().startsWith(symbol))
                .sorted()
                .map(studentMapper::tDto)
                .collect(Collectors.toList());
    }

    public double getAverageAgeByFindAll() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .getAsDouble();
    }

    public int getByParallel() {
        int sum = Stream.iterate(1, a -> a + 1)
                .limit(1000000)
                .parallel()
                .reduce(0, Integer::sum);
        return sum;
    }

    public void getNamesInStreams() {
        List<String> names = getNamesStudents();
        printStudentName(names.get(0));
        printStudentName(names.get(1));

        new Thread(()-> {
            printStudentName(names.get(2));
            printStudentName(names.get(3));

        })
                .start();
        new Thread(() -> {
            printStudentName(names.get(4));
            printStudentName(names.get(5));

        })
                .start();

    }

    public void getNamesInStreamsSynchronized() {
        List<String> names = getNamesStudents();
        printStudentNameSynchronized(names.get(0));
        printStudentNameSynchronized(names.get(1));

        new Thread(()-> {
            printStudentNameSynchronized(names.get(2));
            printStudentNameSynchronized(names.get(3));

        })
                .start();
        new Thread(() -> {
            printStudentNameSynchronized(names.get(4));
            printStudentNameSynchronized(names.get(5));

        })
                .start();

    }
    private void printStudentName(String studentName) {
        try {
            Thread.sleep(1000);
            logger.info(studentName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void printStudentNameSynchronized(String studentName) {
            try {
                Thread.sleep(1000);
                logger.info(studentName + " " + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
    }

    public List<String> getNamesStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::tDto)
                .map(StudentDtoOut::getName)
                .collect(Collectors.toList());
    }

}
