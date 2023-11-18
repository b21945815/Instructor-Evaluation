package Group4.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Optional<Department> findByName(String name);

}
