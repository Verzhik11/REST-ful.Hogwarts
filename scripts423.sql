SELECT student.name, student.age, faculty.name
from student
INNER JOIN faculty ON student.faculty_id = faculty.id ORDER BY age;

SELECT student.name, student.age
from avatar
INNER JOIN student on avatar.student_id = student.id ORDER BY faculty_id, age;
