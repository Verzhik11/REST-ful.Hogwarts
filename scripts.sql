SELECT *
FROM student;

SELECT *
FROM student
WHERE age BETWEEN 18 AND 24;

SELECT name FROM student;

SELECT * FROM student
WHERE name LIKE '%e%';

SELECT *
FROM student
WHERE age < 18;

SELECT * FROM student
ORDER BY age, name;

--additional

SELECT *
FROM faculty;

SELECT faculty.* FROM faculty, student
WHERE student.faculty_id = faculty.id
AND student.name = 'Vlad';

SELECT age, count(age) AS repeats FROM student
GROUP BY age
HAVING count(age) > 1;

SELECT count(age)
FROM student
WHERE age > 17