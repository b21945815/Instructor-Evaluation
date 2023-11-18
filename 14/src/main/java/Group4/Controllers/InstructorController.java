package Group4.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group4.Entities.Course;
import Group4.Entities.Form;
import Group4.Entities.Resource;
import Group4.Entities.Result;
import Group4.Requests.DateRequest;
import Group4.Requests.MultipleChoiceRequest;
import Group4.Requests.NewFormRequest;
import Group4.Requests.OpenEndedRequest;
import Group4.Responses.Response;
import Group4.Services.FormService;
import Group4.Services.RequestService;
import Group4.Services.ResourceService;
import Group4.Services.ResultService;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

	@Autowired
	private RequestService requestService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private FormService formService;
	
	/*
	 * You can get all not sent forms of instructor*/
	@GetMapping("/openForms/{instructorId}")
	public List<Form> getAllOpenFormForInstructor(@PathVariable Long instructorId){
		return formService.getAllOpenFormForUser(instructorId);
	}
	/*
	 * You can get all sent forms of instructor*/
	@GetMapping("/sentForms/{instructorId}")
	public List<Form> getAllSentFormForInstructor(@PathVariable Long instructorId){
		return formService.getAllSentFormForUser(instructorId);
	}
	@GetMapping("/courses/{instructorId}")
	public List<Course> getAllCoursesWithoutForms(@PathVariable Long instructorId){
		return formService.getAllCoursesWithoutForms(instructorId);
	}
	/*
	 * You can get a form of instructor*/
	@GetMapping("/forms/{formId}")
	public Form getAFormForInstructorForOneCourse(@PathVariable Long formId){
		return formService.getFormForInstructor(formId);
	}
	/*
	 * Instructor can open new form*/
	@PostMapping("/addForm")
	public Response addForm(@RequestBody NewFormRequest newForm) {
		return formService.saveOneForm(newForm);
	}
	
	/*
	 * deleting a form but this it breaks the do it later limit, i think we shouldn't allow delete, instead there should be reset key */
	@DeleteMapping("/deleteForm/{id}")
	public Response deleteForm(@PathVariable Long id) {
		return formService.deleteOneForm(id);
	}
	
	/*
	 * Delete one question from form*/
	@DeleteMapping("/deleteQuestion/{id}/{questionId}")
	public Response deleteOneQuestionFromForm(@PathVariable Long id, @PathVariable Long questionId) {
		return formService.deleteOneQuestionFromForm(id, questionId);
	}
	/*
	 * Instructor can add open ended question to the form
	 * Note: id = formId*/
	@PostMapping("/addOpen/{id}")
	public Response addOpenEndedQuestionToForm(@PathVariable Long id, @RequestBody OpenEndedRequest question) {
		return formService.addOpenEndedQuestionToForm(id, question);
	}
	/*
	 * Instructor can add multiple choice question to the form
	 * Note: id = formId*/	
	@PostMapping("/addMultiple/{id}")
	public Response addMultipleChoiceQuestionToForm(@PathVariable Long id, @RequestBody MultipleChoiceRequest question) {
		return formService.addMultipleQuestionToForm(id, question);
	}
	
	/*
	 * Instructor can send ban request to the admin*/
	@PutMapping("/ban/{senderId}/{suspectId}/{message}")
	public Response sendBanRequest(@PathVariable Long senderId, @PathVariable Long suspectId, @PathVariable String message){
		return requestService.banRequest(senderId, suspectId, message);
	}
	/*
	 * Instructor can see the resources that department manager shared*/
	@GetMapping("/viewResources/{departmentId}")
	public List<Resource> viewResources(@PathVariable Long departmentId){
		return resourceService.getAllResourcesForOneDepartment(departmentId);
	}
	
	/*
	 * Instructor can send re evaluation request for form*/
	@PutMapping("/reEvaluate/{departmentId}/{courseId}")
	public Response SendReEvaluationRequest(@PathVariable Long departmentId, @PathVariable Long courseId){
		return resultService.SendReEvaluateRequest(departmentId, courseId);
	}
	/*
	 * Instructor view results for their courses*/
	@GetMapping("/viewResult/{courseId}")
	public Result viewResultForOneCourse(@PathVariable Long courseId){
		return resultService.getResultForOneCourse(courseId);
	}

	/*
	 * Check form if sent to the students*/
	@GetMapping("/checkForm/{FormId}")
	public boolean checkFormIfSend(@PathVariable Long formId){
		return formService.checkFormIfSend(formId);
	}
	/*
	 * Send the form to the students*/
	@PutMapping("sendForm/{formId}")
	public Response sendForm(@PathVariable Long formId){
		return formService.sendFormForOneCourse(formId);
	}
	/*
	 * Get statistical data for one course
	 * Note: Returns the average result for multiple choice questions*/
	@GetMapping("/getStatisticalDataForOneCourse/{courseId}")
	public List<Float> getStatisticalDataForOneCourse(@PathVariable Long courseId) {
		return formService.getStatisticalDataForOneCourse(courseId);
	}
	/*
	 * You need the call this function every time instructor saves the form*/
	@PostMapping("updateNumberOfSaves/{formId}")
	public Response updateNumberOfSaves(@PathVariable Long formId){
		return formService.updateNumberOfSaves(formId);
	}
	/*
	 * You can get number of saves for one form*/
	@GetMapping("/getNumberOfSaves/{formId}")
	public Response getNumberOfSaves(@PathVariable Long formId) {
		return formService.getNumberOfSaves(formId);
	}
	/*
	 * Instructor need to set start and end dates for forms*/
	@PostMapping("setDatesOfForm/{formId}")
	public Response setDatesOfForm(@PathVariable Long formId, @RequestBody DateRequest dates){
		return formService.setDatesOfForm(formId, dates);
	}
	
	/*
	 * Instructor can download results in the form of excel file
	 * Note: This is not implemented*/
	@GetMapping("/getExcelFile/{formId}")
	public Response getExcelFileForOneResult(@PathVariable Long formId) {
		return formService.getExcelFileForOneResult(formId);
	}
	
}
