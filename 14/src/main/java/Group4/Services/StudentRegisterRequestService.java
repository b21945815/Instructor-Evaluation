package Group4.Services;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import Group4.Entities.Course;
import Group4.Entities.Department;
import Group4.Entities.StudentRegisterRequest;
import Group4.Entities.User;
import Group4.Repositories.DepartmentRepository;
import Group4.Repositories.StudentRegisterRequestRepository;
import Group4.Repositories.UserRepository;
import Group4.Responses.Response;


@Service
public class StudentRegisterRequestService {

    @Autowired
    private JavaMailSender mailSender;
    
	@Autowired
	private StudentRegisterRequestRepository studentRegisterRequestRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	
	public Response saveRequest(StudentRegisterRequest registerRequest) {
		Response response = new Response();
		Optional<User> user = userRepository.findById(registerRequest.getId());
		if(user.isPresent()) {
			response.setMessage("There is a user with this id");
			return response;
		}
		user = userRepository.findByMail(registerRequest.getMail());
		if(user.isPresent()) {
			response.setMessage("There is a user with this mail");
			return response;
		}
		Optional<Department> department = departmentRepository.findByName(registerRequest.getDepartment());
		if(department.isEmpty()) {
			response.setMessage("There is not a department with this name");
			return response;
		}
		if(registerRequest.getMail().matches ("[a-zA-Z0-9_\\-\\.]+@(hacettepe|cs\\.hacettepe)\\.edu\\.tr$")) {
			studentRegisterRequestRepository.save(registerRequest);
			response.setMessage("Request saved");
			return response;
		}else {
			response.setMessage("The mail format is wrong");
			return response;
		}
	}
	
	public Response acceptRequest(Long id) {
		Response response = new Response();
		Optional<StudentRegisterRequest> request = studentRegisterRequestRepository.findById(id);
		if(request.isEmpty()) {
			response.setMessage("There is no such request");
			return response;
		}
		StudentRegisterRequest registerRequest = request.get();
		Optional<User> user = userRepository.findByMail(registerRequest.getMail());
		if(user.isPresent()) {
			response.setMessage("There is a user with this mail");
			return response;
		}
		user = userRepository.findById(registerRequest.getId());
		if(user.isPresent()) {
			response.setMessage("There is a user with this id");
			return response;
		}
		Optional<Department> department = departmentRepository.findByName(request.get().getDepartment());
		if(department.isEmpty()) {
			response.setMessage("There is no such department");
			return response;
		}
		User toSave = new User();
		String password = String.valueOf((Math.random()));
		toSave.setType("student");
		toSave.setQuestions(new ArrayList<String>());
		toSave.setMail(registerRequest.getMail());
		toSave.setId(registerRequest.getId());
		toSave.setDepartment(department.get());
		toSave.setBan(false);
		toSave.setCourseList(new ArrayList<Course>());
		toSave.setPassword(passwordEncoder.encode(password));
		userRepository.save(toSave);
		response.setUserId(toSave.getId());
		response.setMessage("User registered successfully");
		studentRegisterRequestRepository.deleteById(request.get().getId());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fatih.ay.616161@gmail.com");
        message.setTo(registerRequest.getMail());
        message.setText("Your account's password is: " + String.valueOf(password));
        message.setSubject("Account information");
        mailSender.send(message);
		return response;
	}

	public List<StudentRegisterRequest> getAllRequests() {
		return studentRegisterRequestRepository.findAll();
	}

	public Response deleteRequest(Long id) {
		Response response = new Response();
		Optional<StudentRegisterRequest> request = studentRegisterRequestRepository.findById(id);
		if(request.isPresent()) {
			studentRegisterRequestRepository.deleteById(request.get().getId());
			response.setMessage("The request is deleted");
			return response;
		}
		response.setMessage("There is no request with this student id");
		return response;
	}

}
