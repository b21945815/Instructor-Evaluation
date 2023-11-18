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
import Group4.Entities.User;
import Group4.Requests.NewResourceRequest;
import Group4.Requests.UpdateCourseRequest;
import Group4.Responses.Response;
import Group4.Services.CourseService;
import Group4.Services.FormService;
import Group4.Services.RequestService;
import Group4.Services.ResourceService;
import Group4.Services.ResultService;
import Group4.Services.UserService;

@RestController
@RequestMapping("/department")
public class DepartmentManagerController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RequestService requestService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private FormService formService;
	
	/*
	 * You can update courses*/
	@PutMapping("/updateCourse")
	public Response updateCourse(@RequestBody UpdateCourseRequest updateCourse) {
		return courseService.updateCourse(updateCourse);
	}
	/*
	 * You can get all instructors for your department*/
	@GetMapping("/{departmentId}/instructors")
	public List<User> getInstructorsForOneDepartment(@PathVariable Long departmentId) {
		return userService.getInstructorForOneDepartment(departmentId);
	}
	/*
	 * You can assign instructors to the courses*/
	@PutMapping("/assignInstructor/{courseId}/{instructorId}")
	public Response assignInstructor(@PathVariable Long courseId, @PathVariable Long instructorId) {
		return userService.assignInstructor(courseId, instructorId);
	}	
	/*
	 * You can dismiss instructors from the courses*/
	@DeleteMapping("/dismissInstructorFromCourse/{courseId}")
	public Response dismissInstructorFromCourse(@PathVariable Long courseId) {
		return userService.deleteCourseInstructor(courseId);
	}	
	/*
	 * You can send ban requests to the admin*/
	@PutMapping("/ban/{senderId}/{suspectId}/{message}")
	public Response sendBanRequest(@PathVariable Long senderId, @PathVariable Long suspectId, @PathVariable String message){
		return requestService.banRequest(senderId, suspectId, message);
	}
	/*
	 * You can view resources that you shared*/
	@GetMapping("/viewResources/{departmentId}")
	public List<Resource> viewResources(@PathVariable Long departmentId){
		return resourceService.getAllResourcesForOneDepartment(departmentId);
	}
	/*
	 * You can upload resources to the system
	 * Note: This can be news, resources or training opportunities*/
	@PutMapping("/uploadResource")
	public Response uploadResource(@RequestBody NewResourceRequest resource){
		return resourceService.saveResource(resource);
	}
	/*
	 * You can delete resources that you shared*/
	@DeleteMapping("/deleteResource/{resourceId}")
	public Response deleteResource(@PathVariable Long resourceId){
		return resourceService.deleteResource(resourceId);
	}
	/*
	 * You can get re evaluation requests for result forms*/
	@GetMapping("/evaluationRequests/{departmentId}/{courseId}")
	public List<Result> getReEvaluationRequests(@PathVariable Long departmentId, @PathVariable Long courseId) {
		return resultService.getReEvaluationRequests(departmentId, courseId);
	}
	/*
	 * You can get students answer for one course*/
	@GetMapping("/getForms/{courseId}")
	public List<Form> getFormsWithAnswersForOneCourse(@PathVariable Long courseId) {
		return formService.getAllAnswerFormForOneCourse(courseId);
	}
	/*
	 * You can open new result form*/
	@PutMapping("/openResultResult/{courseId}")
	public Response openResultForm(@PathVariable Long courseId) {
		return resultService.openResultForm(courseId);
	}
	/*
	 * You can edit a result form*/
	@PostMapping("/editResultResult/{courseId}")
	public Response editResultForm(@PathVariable Long courseId, @RequestBody Result result) {
		return resultService.editResultForm(courseId, result);
	}
	/*
	 * You can send result form*/
	@PostMapping("/sendResultResult/{courseId}")
	public Response sendResultForm(@PathVariable Long courseId) {
		return resultService.sendResultForm(courseId);
	}
	/*
	 * Get statistical data for one course
	 * Note: Returns the average result for multiple choice questions*/
	@GetMapping("/getStatisticalDataForOneCourse/{courseId}")
	public List<Float> getStatisticalDataForOneCourse(@PathVariable Long courseId) {
		return formService.getStatisticalDataForOneCourse(courseId);
	}
	/* You can see courses which you didn't sent results*/
	@GetMapping("/findCoursesWhichResultsAreNotPosted/{departmentId}")
	public List<Course> findCoursesWhichResultsAreNotPosted(@PathVariable Long departmentId) {
		return courseService.findCoursesWhichResultsAreNotPosted(departmentId);
	}
	/*
	 * You can see your result form for one course*/
	@GetMapping("/getResultFormForOneCourse/{courseId}")
	public Result getResultFormForOneCourse(@PathVariable Long courseId) {
		return resultService.getResultFormForOneCourse(courseId);
	}
}
