package Group4.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.Course;
import Group4.Entities.Department;
import Group4.Entities.Result;

public interface ResultRepository extends JpaRepository<Result, Long>  {

	List<Result> findByCourseAndDepartmentAndReEvaluate(Course course, Department department, boolean b);

	Optional<Result> findByCourseAndDepartment(Course course, Department department);

	Optional<Result> findByCourse(Course course);
	
	Optional<Result> findByCourseId(Long courseId);

}
