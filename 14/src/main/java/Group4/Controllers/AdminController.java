package Group4.Controllers;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group4.Entities.Form;
import Group4.Entities.Request;
import Group4.Entities.StudentRegisterRequest;
import Group4.Entities.User;
import Group4.Requests.DateRequest;
import Group4.Requests.NewCourseRequest;
import Group4.Requests.NewUserRequest;
import Group4.Responses.Response;
import Group4.Services.CourseService;
import Group4.Services.DateInformationService;
import Group4.Services.DepartmentService;
import Group4.Services.FormService;
import Group4.Services.RequestService;
import Group4.Services.StudentRegisterRequestService;
import Group4.Services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private RequestService requestService;
	
	@Autowired
	private StudentRegisterRequestService studentRegisterRequestService; 
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private DateInformationService dateInformationService;

	/*
	 * You can add users manually
	 * Note: department is need to be not null*/
	@PostMapping("/addUser")
	public Response addUser(@RequestBody NewUserRequest newUserRequest) {
		return userService.saveOneUser(newUserRequest);
	}
	/*
	 * You can add courses manually
	 * Note: department is need to be not null*/
	@PostMapping("/addCourse")
	public Response addCourse(@RequestBody NewCourseRequest newCourse) {
		return courseService.saveOneCourse(newCourse);
	}
	/*
	 * You can delete courses but be careful about foreign keys*/
	@DeleteMapping("/deleteCourse/{id}")
	public Response deleteCourse(@PathVariable Long id) {
		return courseService.deleteOneCourse(id);
	}
	/* 
	 * You can add a department*/
	@PostMapping("/addDepartment/{name}")
	public Response addDepartment(@PathVariable String name) {
		return departmentService.saveOneDepartment(name);
	}
	
	/* 
	 * You can delete a department but be careful about foreign keys*/
	@DeleteMapping("/deleteDepartment/{name}")
	public Response deleteDepartment(@PathVariable String name) {
		return departmentService.deleteOneDepartment(name);
	}
	/*
	 * You can see register requests*/
	@GetMapping("/registerRequests")
	public List<StudentRegisterRequest> getAllStudentRequests(){
		return studentRegisterRequestService.getAllRequests();
	}
	/* You can accept register requests
	 * Note: id = student id*/
	@PostMapping("/acceptRequest/{id}")
	public Response acceptRequest(@PathVariable Long id) {
		return studentRegisterRequestService.acceptRequest(id);
	}
	/* You can delete register requests
	 * Note: id = student id*/
	@DeleteMapping("/deleteRequest/{id}")
	public Response deleteRequest(@PathVariable Long id) {
		return studentRegisterRequestService.deleteRequest(id);
	}
	/*
	 *  You can see all users*/
	@GetMapping("/allUsers")
	public List<User> getAllUsers(){
		return userService.getAllUsers();
	}
	/*
	 *  You can delete users but be careful about foreign keys*/
	@DeleteMapping("/deleteUser/{id}")
	public Response deleteOneUser(@PathVariable Long id) {
		return userService.deleteOneUser(id);
	}
	/*
	 *  You can ban users*/
	@PutMapping("/banUser/{requestId}")
	public Response banUser(@PathVariable Long requestId) {
		return requestService.acceptBanRequest(requestId);
	}
	/*
	 * You can remove ban from users*/
	@PutMapping("/removeBan/{userId}")
	public Response removeBan(@PathVariable Long userId) {
		return userService.removeBan(userId);
	}
	/*
	 * You can see ban request from instructors and department managers*/
	@GetMapping("/getBanRequests")
	public List<Request> getBanRequests() {
		return requestService.getBanRequests();
	}
	/*
	 * You can change semester start date*/
	@PutMapping("/changeSemesterStartDate")
	public Response changeStartDate(@RequestBody DateRequest dateRequest) {
		return dateInformationService.setSemesterStartDate(dateRequest);
	}
	/*
	 * You can change semester finish date*/
	@PutMapping("/changeSemesterEndDate")
	public Response changeEndDate(@RequestBody DateRequest dateRequest) {
		return dateInformationService.setSemesterFinishDate(dateRequest);
	}
	/*
	 * You can get all forms that instructors sent*/
	@GetMapping("/getAllForms")
	public List<Form> getAllSentForms() {
		return formService.getAllSentFormsFromTeachers();
	}
	/*
	 *You can delete inappropriate questions from forms*/
	@DeleteMapping("/deleteQuestionFromForm/{formId}/{questionId}")
	public Response deleteQuestionFromForm(@PathVariable Long formId, @PathVariable Long questionId) {
		return formService.deleteOneQuestionFromForm(formId, questionId);		
	}
	/*
	 * You can set detailed results date
	 * Detailed results date: Before this day, department managers can not see students' feedbacks*/
	@PostMapping("/setDetailedResultsDate")
	public Response setDetailedResultsDate(@RequestBody DateRequest dateRequest) {
		return dateInformationService.setDetailedResultsDate(dateRequest);		
	}
	/*
	 * admin need to use this function daily
	 * This is sent some regular mails*/
	@PostMapping("/sendRegularMails")
	public Response sendRegularMails() {
		try {
			return formService.sendRegularMails();
		} catch (MessagingException e) {
			Response response = new Response();
			response.setMessage(e.toString());
			return response;
		}
	}
	/*
	 * You can set evaluation date
	 * Evaluation date: Before this day, students can not gave feedback/edit forms*/
	@PostMapping("/setEvaluationDate")
	public Response setEvaluationDate(@RequestBody DateRequest dateRequest) {
		return dateInformationService.setEvaluationDate(dateRequest);		
	}
}
