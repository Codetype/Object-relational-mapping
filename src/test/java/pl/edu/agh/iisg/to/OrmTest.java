package pl.edu.agh.iisg.to;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.session.SessionService;

public class OrmTest {

    private final StudentDao studentDao = new StudentDao();

    private final CourseDao courseDao = new CourseDao();

    private final GradeDao gradeDao = new GradeDao();
    
    @Before
    public void before() {
    	SessionService.openSession();
    }
    
    @After
    public void after() {
    	SessionService.closeSession();
    }

    @Test
    public void createStudentTest() {
        Optional<Student> first = studentDao.create("Adam", "Kowalski", 100122);
        checkStudent(first);
        Optional<Student> second = studentDao.create("Jan", "Nowak", 100123);
        checkStudent(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
        Optional<Student> third = studentDao.create("Kasia", "Kowalska", 100123);
        Assert.assertTrue(!third.isPresent());
    }


    @Test 
    public void findStudentIndexTest() {
        Optional<Student> first = studentDao.create("Kasia", "Kowalska", 300124);
        checkStudent(first);
        Optional<Student> second = studentDao.findByIndexNumber(first.get().indexNumber());
        Assert.assertEquals(first.get(), second.get());
    }

    @Test
    public void createCourseTest() {
        Optional<Course> first = courseDao.create("TO");
        checkCourse(first);
        Optional<Course> second = courseDao.create("TO2");
        checkCourse(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
        Optional<Course> third = courseDao.create("TO2");
        Assert.assertTrue(!third.isPresent());
    }

    @Test
    public void findCourseTest() {
        Optional<Course> first = courseDao.create("TK");
        checkCourse(first);
        Optional<Course> second = courseDao.findById(first.get().id());
        Assert.assertEquals(first.get(), second.get());
    }

    @Test
    public void enrollStudentTest() {
        Optional<Student> first = studentDao.create("Kasia", "Kowalska", 700124);
        checkStudent(first);
        Optional<Course> second = courseDao.create("MOWNIT");
        checkCourse(second);
        Assert.assertTrue(courseDao.enrollStudent(second.get(), first.get()));
        Assert.assertFalse(courseDao.enrollStudent(second.get(), first.get()));
        Assert.assertTrue(second.get().studentSet().contains(first.get()));
        Assert.assertTrue(first.get().courseSet().contains(second.get()));
    }

    @Test
    public void courseStudentListTest() {
        Optional<Student> first = studentDao.create("Adam", "Paciaciaczek", 800125);
        checkStudent(first);
        Optional<Student> second = studentDao.create("Jan", "Paciaciaczek", 800126);
        checkStudent(second);
        Optional<Course> third = courseDao.create("WDI");
        checkCourse(third);
        Assert.assertTrue(courseDao.enrollStudent(third.get(), first.get()));
        Assert.assertTrue(courseDao.enrollStudent(third.get(), second.get()));
        Set<Student> students = third.get().studentSet();
        Assert.assertEquals(2, students.size());
        Assert.assertTrue(students.contains(first.get()));
        Assert.assertTrue(students.contains(second.get()));
    }

    @Test
    public void gradeStudentTest() {
        Optional<Student> first = studentDao.create("Kasia", "Kowalska", 900124);
        checkStudent(first);
        Optional<Course> second = courseDao.create("MOWNIT 2");
        checkCourse(second);
        Assert.assertEquals(0, first.get().gradeSet().size());
        Assert.assertTrue(gradeDao.gradeStudent(first.get(), second.get(), 5.0f));
        Assert.assertEquals(1, first.get().gradeSet().size());
    }

    @Test
    public void createReportTest() {
        Optional<Student> first = studentDao.create("Kasia", "Kowalska", 1000124);
        checkStudent(first);
        Optional<Course> second = courseDao.create("Bazy");
        checkCourse(second);
        Assert.assertTrue(gradeDao.gradeStudent(first.get(), second.get(), 5.0f));
        Assert.assertTrue(gradeDao.gradeStudent(first.get(), second.get(), 4.0f));
        Optional<Course> third = courseDao.create("Bazy 2");
        checkCourse(third);
        Assert.assertTrue(gradeDao.gradeStudent(first.get(), third.get(), 5.0f));
        Assert.assertTrue(gradeDao.gradeStudent(first.get(), third.get(), 3.0f));
        Map<Course, Float> report = studentDao.createReport(first.get());
        Assert.assertTrue(Float.compare(4.5f, report.get(second.get())) == 0);
        Assert.assertTrue(Float.compare(4.0f, report.get(third.get())) == 0);
    }

    private void checkStudent(final Optional<Student> student) {
        Assert.assertTrue(student.isPresent());
        student.ifPresent(s -> {
            Assert.assertTrue(s.id() > 0);
            Assert.assertNotNull(s.firstName());
            Assert.assertNotNull(s.lastName());
            Assert.assertTrue(s.indexNumber() > 0);
        });
    }

    private void checkCourse(final Optional<Course> course) {
        Assert.assertTrue(course.isPresent());
        course.ifPresent(c -> {
            Assert.assertTrue(c.id() > 0);
            Assert.assertNotNull(c.name());
        });
    }

}
