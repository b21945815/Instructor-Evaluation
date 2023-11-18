package Group4.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.Department;
import Group4.Repositories.DepartmentRepository;
import Group4.Responses.Response;

@Service
public class DepartmentService {

	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	public List<Department> getAllDepartments() {
		return departmentRepository.findAll();
	}


	public Response saveOneDepartment(String name) {
		Response response = new Response();
		if(!name.matches ("[a-zA-Z ]+$")) {
			response.setMessage("The department name is not suitable");
			return response;
		}
		if(departmentRepository.findByName(name).isPresent()){
			response.setMessage("There is a department with this name");
			return response;
		}
		Department department = new Department();
		department.setName(name);
		departmentRepository.save(department);
		response.setMessage("The department saved");
		return response;
	}


	public Response deleteOneDepartment(String name) {
		Response response = new Response();
		Optional<Department> department = departmentRepository.findByName(name);
		if(department.isEmpty()) {
			response.setMessage("There is not a department with this name");
			return response;
		}
		departmentRepository.deleteById(department.get().getId());
		response.setMessage("The department is deleted");
		return response;
	}

}
