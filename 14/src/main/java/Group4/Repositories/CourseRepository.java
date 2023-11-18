package Group4.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findByDepartmentId(Long departmentId);



}
