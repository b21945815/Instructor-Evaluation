package Group4.Services;

import java.util.ArrayList;
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
import Group4.Responses.Response;

@Service
public class ResultService {

	@Autowired
	private ResultRepository resultRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	public List<Result> getReEvaluationRequests(Long departmentId, Long courseId) {
		Optional<Department> department = departmentRepository.findById(departmentId);
		if(department.isEmpty()) {
			return new ArrayList<Result>();
		}
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isEmpty()) {
			return new ArrayList<Result>();
		}
		return resultRepository.findByCourseAndDepartmentAndReEvaluate(course.get(), department.get(), true);
	}

	public Response SendReEvaluateRequest(Long departmentId, Long courseId) {
		Response response = new Response();
		Optional<Department> department = departmentRepository.findById(departmentId);
		if(department.isEmpty()) {
			response.setMessage("There is no department with this id");
			return response;
		}
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isEmpty()) {
			response.setMessage("There is no course with this id");
			return response;
		}
		Optional<Result> result = resultRepository.findByCourseAndDepartment(course.get(), department.get());
		if(result.isEmpty()) {
			response.setMessage("There is no result for this course");
			return response;
		}
		result.get().setReEvaluate(true);
		resultRepository.save(result.get());
		response.setMessage("The re-Evaluation request is sent");
		return response;
	}

	public Result getResultForOneCourse(Long courseId) {
		Optional<Result> result = resultRepository.findByCourseId(courseId);
		if(result.isEmpty()) {
			return null;
		}
		if(!result.get().getSent()) {
			return null;
		}
		return result.get();
	}
	
	public Response openResultForm(Long courseId) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isEmpty()) {
			response.setMessage("There is no course with this id");
			return response;
		}
		Department department = course.get().getDepartment();
		Result result = new Result();
		result.setDepartment(department);
		result.setCourse(course.get());
		result.setSent(false);
		result.setReEvaluate(false);
		resultRepository.save(result);
		response.setMessage("The form is sent");
		return response;
	}

	public Response sendResultForm(Long courseId) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isEmpty()) {
			response.setMessage("There is no course with this id");
			return response;
		}
		Optional<Result> result = resultRepository.findByCourse(course.get());
		if(result.isEmpty()) {
			response.setMessage("There is no result form for this course");
			return response;
		}
		if(result.get().getSent()) {
			response.setMessage("The result form was already sent");
			return response;
		}
		result.get().setSent(true);
		resultRepository.save(result.get());
		response.setMessage("The result form is sent");
		return response;
	}

	public Response editResultForm(Long courseId, Result result) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isEmpty()) {
			response.setMessage("There is no course with this id");
			return response;
		}
		if(result.getSent() && !result.getReEvaluate()) {
			response.setMessage("The result form was already sent");
			return response;
		}
		result.setReEvaluate(false);
		resultRepository.save(result);
		response.setMessage("The result form is edited");
		return response;
	}

	public Result getResultFormForOneCourse(Long courseId) {
		Optional<Result> result = resultRepository.findByCourseId(courseId);
		if(result.isEmpty()) {
			return null;
		}
		return result.get();
	}
}
