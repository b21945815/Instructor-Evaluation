package Group4.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.Department;
import Group4.Entities.Resource;

public interface ResourceRepository  extends JpaRepository<Resource, Long>{

	List<Resource> findByDepartment(Department department);

}
