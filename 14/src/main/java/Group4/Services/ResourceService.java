package Group4.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.Department;
import Group4.Entities.Resource;
import Group4.Repositories.DepartmentRepository;
import Group4.Repositories.ResourceRepository;
import Group4.Requests.NewResourceRequest;
import Group4.Responses.Response;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
	
	public List<Resource> getAllResourcesForOneDepartment(Long departmentId) {
		Optional<Department> department = departmentRepository.findById(departmentId);
		if(department.isEmpty()) {
			new ArrayList<Resource>();
		}
		return resourceRepository.findByDepartment(department.get());
	}

	public Response saveResource(NewResourceRequest resource) {
		Response response = new Response();
		Optional<Department> department = departmentRepository.findById(resource.getDepartmentId());
		if(department.isEmpty()) {
			response.setMessage("There is no department with this id");
			return response;
		}
		Resource newResource = new Resource();
		newResource.setDepartment(department.get());
		newResource.setLink(resource.getLink());
		newResource.setMessage(resource.getMessage());
		newResource.setTitle(resource.getTitle());
		resourceRepository.save(newResource);
		response.setMessage("The resource is saved");
		return response;
	}

	public Response deleteResource(Long resourceId) {
		Response response = new Response();
		Optional<Resource> resource = resourceRepository.findById(resourceId);
		if(resource.isEmpty()) {
			response.setMessage("There is no resource with this id");
			return response;
		}
		resourceRepository.deleteById(resourceId);
		response.setMessage("The resource is deleted");
		return response;
	}
}
