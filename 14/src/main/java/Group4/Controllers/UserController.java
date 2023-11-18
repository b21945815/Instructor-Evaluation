package Group4.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Group4.Entities.Course;
import Group4.Entities.DateInformation;
import Group4.Entities.Department;
import Group4.Entities.User;
import Group4.Requests.UpdateUserRequest;
import Group4.Responses.Response;
import Group4.Services.DateInformationService;
import Group4.Services.DepartmentService;
import Group4.Services.FormService;
import Group4.Services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private DateInformationService dateInformationService;
	/*
	 * You can get user information with id*/
	@GetMapping("/{userId}")
	public User getUser(@PathVariable Long userId) {
		return userService.getOneUserById(userId);
	}
	/*
	 * You can get course list of one user*/
	@GetMapping("/myCourses/{userId}")
	public List<Course> getCoursesForOneUser(@PathVariable Long userId) {
		return userService.getCoursesForOneUser(userId);
	}
	/*
	 * You can update user information
	 * Note: Not all information can change with this function, check UpdateUserRequest object*/
	@PutMapping
	public Response updateUser(@RequestBody UpdateUserRequest updateUser) {
		return userService.updateUser(updateUser);
	}
	
	/* You can get courses for one department
	 * This is for 
	 * 1- When a student wants to add a course
	 * 2- When a department manager wants to assign a instructor to a course*/
	@GetMapping("/courses/{departmentId}")
	public List<Course> getCoursesForOneDepartment(@PathVariable Long departmentId) {
		return userService.getCoursesForOneDepartment(departmentId);
	}
	/*
	 * To get example questions from department manager
	 * This is for instructors 
	 * To use this function, you provide your own userId and thus you get the questions registered by your department's manager.*/
	@GetMapping("/questionsFromDM/{userId}")
	public List<String> questionsFromDM(@PathVariable Long userId) {
		return userService.questionsFromDM(userId);
	}
	
	/*
	 * This is for department managers to add example questions*/
	@GetMapping("/addQuestionToResource/{userId}")
	public Response addQuestionToResource(@PathVariable Long userId, @RequestBody String question) {
		return userService.addQuestionToResource(userId, question);
	}
	/*
	 * You can get semester start date for calendar, for showing to admin etc.*/
	@GetMapping("/getSemesterStartDate")
	public DateInformation getSemesterStartDate() {
		return dateInformationService.getSemesterStartDate();
	}
	/*
	 * You can get semester end date for calendar, for showing to admin etc.*/
	@GetMapping("/getSemesterEndDate")
	public DateInformation getSemesterEndDate() {
		return dateInformationService.getSemesterFinishDate();
	}
	/*
	 * You can get percent of completion of forms for one course for instructor, department manager or admin*/
	@GetMapping("/percentOfCompletion/{courseId}")
	public Float percentOfCompletion(@PathVariable Long courseId) {
		return formService.percentOfCompletion(courseId);
	}
	/*
	 * You can get detailed results date for calendar, for showing to admin etc.
	 * Before this day, the department manager cannot see the forms filled by the students.*/	
	@GetMapping("/getDetailedResultsDate")
	public DateInformation getDetailedResultsDate() {
		return dateInformationService.getDetailedResultsDate();		
	}
	/*
	 * You can get Evaluation date for calendar, for showing to admin etc.
	 * Before this day, the student can not gave feedback.*/		
	@GetMapping("/getEvaluationDate")
	public DateInformation getEvaluationDate() {
		return dateInformationService.getEvaluationDate();		
	}
	/*
	 * You can change profile picture*/
	@PutMapping("/changeProfilePicture/{userId}")
	public Response changeProfilePicture(@RequestBody MultipartFile image, @PathVariable Long userId) throws IOException {
		try {
			return userService.changeProfilePicture(image, userId);
		}catch(Exception e) {
			Response response = new Response();
			response.setMessage("There is a error");
			return response;
		}
	}
	/*
	 * You can get profile picture*/
	@GetMapping("/getProfilePicture")
	public byte[] getProfilePicture(@PathVariable Long userId) {
		return userService.getProfilePicture(userId);
	}
	
	/* You can get department list*/
	@GetMapping("/getDepartments")
	public List<Department> getDepartments() {
		return departmentService.getAllDepartments();
	}
}