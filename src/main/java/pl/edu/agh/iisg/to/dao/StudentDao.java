package pl.edu.agh.iisg.to.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Student;

import javax.persistence.PersistenceException;

public class StudentDao extends GenericDao<Student> {

    public Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        try {
            save(new Student(firstName, lastName, indexNumber));
            return findByIndexNumber(indexNumber);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Student> findByIndexNumber(final int indexNumber) {
        try {
            Student student = currentSession().createQuery("SELECT s FROM Student s WHERE s.indexNumber = :indexNumber", Student.class)
                    .setParameter("indexNumber", indexNumber)
                    .getSingleResult();
            return Optional.of(student);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Map<Course, Float> createReport(final Student student) {
        Map<Course, Float> resultMap = new HashMap<>();
        Map<Course, Float> reportMap = new HashMap<>();
        Map<Course, Integer> gradesNumber = new HashMap<>();

        try{
            student.gradeSet().forEach(g -> {
                float gradesSum = reportMap.getOrDefault(g.course(), 0f);
                reportMap.put(g.course(),gradesSum + g.grade());
                int count = gradesNumber.getOrDefault(g.course(), 0);
                gradesNumber.put(g.course(), count + 1);
            });
            reportMap.forEach((course, sum) -> {
                float mean = sum/gradesNumber.get(course);
                resultMap.put(course, mean);
            });
            return resultMap;
        } catch (PersistenceException e) {
            return Collections.emptyMap();
        }
    }

}
