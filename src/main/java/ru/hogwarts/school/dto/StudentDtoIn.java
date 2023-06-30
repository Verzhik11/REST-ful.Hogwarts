package ru.hogwarts.school.dto;

public class StudentDtoIn {
    private String name;
    private int age;
    private long faculty_id;

    public StudentDtoIn(String name, int age, long faculty_id) {
        this.name = name;
        this.age = age;
        this.faculty_id = faculty_id;
    }

    public long getFaculty_id() {
        return faculty_id;
    }

    public void setFaculty_id(long faculty_id) {
        this.faculty_id = faculty_id;
    }


    public StudentDtoIn() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
