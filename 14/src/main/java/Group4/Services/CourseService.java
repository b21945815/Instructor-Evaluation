package Group4.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.Course;
import Group4.Entities.Department;
import Group4.Entities.Result;
import Group4.Repositories.CourseRepository;
import Group4.Repositories.DepartmentRepository;
import Group4.Repositories.ResultRepository;
import Group4.Requests.NewCourseRequest;
import Group4.Requests.UpdateCourseRequest;
import Group4.Responses.Response;

@Service
public class CourseService {

	public static final String[] TYPES = new String[] {"mandatory","optional"};
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private ResultRepository resultRepository;
	
	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	public Response saveOneCourse(NewCourseRequest newCourse) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(newCourse.getId());
		if(!course.isEmpty()) {
			response.setMessage("There is a course with this id");
			return response;
		}
		String type = newCourse.getType();
		boolean contains = Arrays.stream(TYPES).anyMatch(type::equals);
		if(!contains) {
			response.setMessage("The course type is wrong");
			return response;
		}
		Optional<Department> department = departmentRepository.findByName(newCourse.getDepartment());
		if(department.isPresent()) {
			if(newCourse.getName().matches ("[a-zA-Z ]+$")) {
				Course toSave = new Course();
				toSave.setId(newCourse.getId());
				toSave.setCredit(newCourse.getCredit());
				toSave.setName(newCourse.getName());
				toSave.setQuota(newCourse.getQuota());
				toSave.setDepartment(department.get());
				toSave.setType(type);
				toSave.setInstructorId(0L);
				courseRepository.save(toSave);
				response.setMessage("The new course saved");
			}else {
				response.setMessage("The name format is wrong");
			}
		}else {
			response.setMessage("There is no such department");
		}
		
		return response;
	}

	public Response updateCourse(UpdateCourseRequest updateCourse) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(updateCourse.getId());
		if(course.isEmpty()) {
			response.setMessage("There is not a course with this id");
			return response;
		}
		Course toUpdate = course.get();
		if(updateCourse.getName().matches ("[a-zA-Z ]+$")) {
			toUpdate.setName(updateCourse.getName());
		}else {
			response.setMessage("The name format is wrong");
			return response;
		}
		if(updateCourse.getCredit() != null) {
			toUpdate.setCredit(updateCourse.getCredit());
		}
		if(updateCourse.getQuota() != null) {
			toUpdate.setQuota(updateCourse.getQuota());
		}
		courseRepository.save(toUpdate);
		response.setMessage("The course is updated");
		return response;
		
	}

	public Response deleteOneCourse(Long id) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(id);
		if(course.isEmpty()) {
			response.setMessage("There is not a course with this id");
			return response;
		}
		courseRepository.deleteById(course.get().getId());
		response.setMessage("The course is deleted");
		return response;
	}

	public List<Course> findCoursesWhichResultsAreNotPosted(Long departmentId) {
		List<Course> courses = courseRepository.findByDepartmentId(departmentId);
		List<Course> result = new ArrayList<Course>();
		for(int i = 0; i < courses.size(); i++) {
			Course course = courses.get(i);
			Optional<Result> resultForm = resultRepository.findByCourse(course); 
			if(resultForm.isEmpty() || !resultForm.get().getSent()) {
				result.add(course);
			}
		}
		return result;
	}

}
