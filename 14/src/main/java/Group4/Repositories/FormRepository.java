package Group4.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Group4.Entities.Form;
import lombok.NonNull;

public interface FormRepository extends JpaRepository<Form, Long> {

	List<Form> findByUserId(Long id);

	Optional<Form> findByUserIdAndCourseId(@NonNull Long userId, @NonNull Long courseId);

	List<Form> findByCourseId(Long courseId);

	List<Form> findByCourseIdAndSent(Long courseId, boolean b);

	List<Form> findAllBySent(boolean b);

	@Query("FROM Form b where b.user.type = :type")
	List<Form> findByUserType(@Param("type") String type);

	List<Form> findByUserIdAndSent(Long instructorId, boolean b);

	List<Form> findByUserIdAndCourseIdAndSent(Long courseId, Long courseId2, boolean b);

}
