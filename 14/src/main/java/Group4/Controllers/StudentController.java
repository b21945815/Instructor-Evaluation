package Group4.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group4.Entities.Course;
import Group4.Entities.Form;
import Group4.Entities.Question;
import Group4.Responses.Response;
import Group4.Services.FormService;
import Group4.Services.UserService;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private FormService formService;
	
	@Autowired
	private UserService userService;
	
	/*
	 * You can add course */
	@PostMapping("/addCourse/{courseId}/{userId}")
	public Response addCourse(@PathVariable Long courseId, @PathVariable Long userId) {
		return userService.addCourseForStudent(courseId, userId);
	}
	/*
	 * You can delete course */
	@DeleteMapping("/deleteCourse/{courseId}/{userId}")
	public Response deleteCourse(@PathVariable Long courseId, @PathVariable Long userId) {
		return userService.deleteCourseForStudent(courseId, userId);
	}
	/*
	 * You can get all not sent forms of instructor*/
	@GetMapping("/openForms/{studentId}")
	public List<Form> getAllOpenFormForStudent(@PathVariable Long studentId){
		return formService.getAllOpenFormForUser(studentId);
	}
	/*
	 * You can get all sent forms of instructor*/
	@GetMapping("/sentForms/{studentId}")
	public List<Form> getAllSentFormForStudent(@PathVariable Long studentId){
		return formService.getAllSentFormForUser(studentId);
	}
	/*
	 * Students get all courses from one department that he/she hasn't taken*/
	@GetMapping("/{departmentId}/getCourses/{studentId}")
	public List<Course> getAllCoursesForOneDepartment(@PathVariable Long departmentId, @PathVariable Long studentId){
		return userService.getAllCoursesForOneDepartment(departmentId, studentId);
	}
	/*
	 * Student can save their form
	 * Note: final answer = multiple choice answer index + 1
	 * For example if index of answer 0 final answer is 1 
	 * * There is bug in this one*/		
	@PostMapping("/saveForm")
	public Response saveFormForOneCourse(@RequestBody Form form) {
		return formService.saveOneFormForStudent(form);
	}
	/*
	 * Student can save their form
	 * Note: final answer = multiple choice answer index + 1
	 * For example if index of answer 0 final answer is 1 */		
	@PostMapping("/saveForm/{formId}")
	public Response saveFormForOneCourse(@RequestBody List<Question> questions, @PathVariable Long formId) {
		return formService.saveOneFormForStudent(questions, formId);
	}
	/*
	 * Student can sent their form 
	 * There is bug in this one*/
	@PostMapping("/sentForm")
	public Response sentFormForOneCourse(@RequestBody Form form) {
		return formService.sentOneFormForStudent(form);
	}
	/*
	 * Student can sent their form */
	@PostMapping("/sentForm/{FormId}")
	public Response sentFormForOneCourse(@RequestBody List<Question> questions, @PathVariable Long formId) {
		return formService.sentOneFormForStudent(questions, formId);
	}
	/*
	 * Student can get how many times she/he saved a form
	 * Note: check while showing forms, don't let her/his edit if she/he sent it  */
	@GetMapping("/getNumberOfSaves/{formId}")
	public Response getNumberOfSaves(@PathVariable Long formId) {
		return formService.getNumberOfSaves(formId);
	}
	/*
	 * Student can check if form sent
	 * Note: check while showing forms, don't let her/his edit if she/he sent it */
	@GetMapping("/checkForm/{FormId}")
	public boolean checkFormIfSend(@PathVariable Long formId){
		return formService.checkFormIfSend(formId);
	}
	/*
	 *Emergency */
	@PostMapping("/saveOpenEndedQuestion/{formId}/{questionId}")
	public Response saveOpenEndedQuestion(@RequestBody String answer, @PathVariable Long formId, @PathVariable Long questionId) {
		return formService.saveOpenEndedQuestion(answer, formId, questionId);
	}
	/*
	 *Emergency */
	@PostMapping("/saveMultipleChoiceQuestion/{formId}/{questionId}/{finalAnswer}")
	public Response saveMultipleChoiceQuestion(@PathVariable Long finalAnswer, @PathVariable Long formId, @PathVariable Long questionId) {
		return formService.saveMultipleChoiceQuestion(finalAnswer, formId, questionId);
	}
	/*
	 *Emergency */
	@PostMapping("/sentFormFinal/{formId}")
	public Response sentFormForOneCourse(@PathVariable Long formId) {
		return formService.sentOneFormForStudent(formId);
	}
}
