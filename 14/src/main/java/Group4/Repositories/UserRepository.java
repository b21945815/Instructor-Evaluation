package Group4.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Group4.Entities.Department;
import Group4.Entities.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByMail(String mail);
	List<User> findByTypeAndDepartment(String string, Department department);
	Optional<User> findByIdAndType(Long studentId, String string);
	
	@Query("FROM User b join b.courseList u where u.id = :courseId")
	List<User> findByCourse(@Param("courseId") Long courseId);
	@Query("FROM User b join b.courseList u where u.id = :courseId and b.id = :studentId")
	List<User> findByCourseAndId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
	List<User> findByType(String string);
}
