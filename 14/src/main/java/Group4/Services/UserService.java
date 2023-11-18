package Group4.Services;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import Group4.Entities.Course;
import Group4.Entities.DateInformation;
import Group4.Entities.Department;
import Group4.Entities.Form;
import Group4.Entities.User;
import Group4.Repositories.CourseRepository;
import Group4.Repositories.DateInformationRepository;
import Group4.Repositories.DepartmentRepository;
import Group4.Repositories.FormRepository;
import Group4.Repositories.UserRepository;
import Group4.Requests.NewUserRequest;
import Group4.Requests.UpdateUserRequest;
import Group4.Responses.Response;


@Service
public class UserService {
	
	
	public static final String[] TYPES = new String[] {"admin","DM","student","instructor"};
	
    @Autowired
    private JavaMailSender mailSender;
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FormRepository formRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private DateInformationRepository dateInformationRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	
	public Response saveOneUser(NewUserRequest newUser) {
		Response response = new Response();
		Optional<User> user = userRepository.findByMail(newUser.getMail());
		if(!user.isEmpty()) {
			response.setMessage("There is a user with this mail");
			return response;
		}
		user = userRepository.findById(newUser.getId());
		if(!user.isEmpty()) {
			response.setMessage("There is a user with this id");
			return response;
		}
		String type = newUser.getType();
		boolean contains = Arrays.stream(TYPES).anyMatch(type::equals);
		if(!contains) {
			response.setMessage("The user type is wrong");
			return response;
		}
		Optional<Department> department = departmentRepository.findByName(newUser.getDepartment());
		if(department.isEmpty()) {
			response.setMessage("There is no such department");
			return response;
		}
		if(department.isPresent()) {
			User toSave = new User();
			if(newUser.getMail().matches ("[a-zA-Z0-9\\_\\-\\.]+@(hacettepe|cs\\.hacettepe)\\.edu\\.tr$")) {
				toSave.setMail(newUser.getMail());
			}else {
				response.setMessage("The mail format is wrong");
				return response;
			}

			String password = String.valueOf((Math.random()));
			toSave.setQuestions(new ArrayList<String>());
			toSave.setPassword(passwordEncoder.encode(password));
			toSave.setDepartment(department.get());
			toSave.setType(type);
			toSave.setBan(false);
			toSave.setCourseList(new ArrayList<Course>());
			toSave.setId(newUser.getId());
			userRepository.save(toSave);
			response.setUserId(newUser.getId());
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("fatih.ay.616161@gmail.com");
	        message.setTo(newUser.getMail());
	        message.setText("Your account's password is: " + String.valueOf(password));
	        message.setSubject("Account information");
	        mailSender.send(message);
			response.setMessage("The new user saved");
		}else {
			response.setMessage("There is no such department");
		}
		return response;

	}

	public User getOneUserById(Long userId) {
		return userRepository.findById(userId).orElse(null);
	}


	public Response updateUser(UpdateUserRequest updateUser) {
		Response response = new Response();
		Optional<User> user = userRepository.findById(updateUser.getId());
		User toUpdate = user.get();
		if(updateUser.getMail() != null) {
			if(updateUser.getMail().matches ("[a-zA-Z0-9\\_\\-\\.]+@(hacettepe|cs\\.hacettepe)\\.edu\\.tr$")) {
				toUpdate.setMail(updateUser.getMail());
			}else {
				response.setMessage("The mail format is wrong");
				return response;
			}
		}
		if(updateUser.getName() != null) {
			if(updateUser.getName().matches ("[A-ZİĞÜŞÇÖ][a-zğüşçöı]+(\\s[A-ZİĞÜŞÇÖ][a-zğüşçöı]+)?$")) {
				toUpdate.setName(updateUser.getName());
			}else {
				response.setMessage("The name format is wrong");
				return response;
			}
		}
		if(updateUser.getSurname() != null) {
			if(updateUser.getSurname().matches ("[A-ZİĞÜŞÇÖ][a-zğüşçöı]+(\\s[A-ZİĞÜŞÇÖ][a-zğüşçöı]+)?$")) {
				toUpdate.setSurname(updateUser.getSurname());
			}else {
				response.setMessage("The surname format is wrong");
				return response;
			}
		}
		if(updateUser.getPhone() != null) {
			if(updateUser.getPhone().matches ("[0-9]{2,2}-[0-9]{3,3}-[0-9]{3,3}-[0-9]{2,2}-[0-9]{2,2}$")) {
				toUpdate.setPhone(updateUser.getPhone());
			}else {
				response.setMessage("The phone number format is wrong");
				return response;
			}
		}
		if(updateUser.getPassword().matches ("[a-zA-Z0-9\\.]+$")) {
			toUpdate.setPassword(passwordEncoder.encode(updateUser.getPassword()));
		}else {
			response.setMessage("The password format is wrong");
			return response;
		}
		response.setMessage("The user is updated");
		userRepository.save(toUpdate);
		return response;
	}


	public Response deleteOneUser(Long id) {
		Response response = new Response();
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			response.setMessage("There is not an user with this id");
			return response;
		}
		userRepository.deleteById(id);
		response.setMessage("The user is deleted");
		return response;
	}


	public Optional<User> getOneUserByMail(String mail) {
		return userRepository.findByMail(mail);
	}


	public Response updateCourseList(Long courseId, Long studentId) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isPresent()) {
			Optional<User> user = userRepository.findByIdAndType(studentId, "student");
			if(user.isEmpty()) {
				response.setMessage("The student information is wrong");
				return response;
			}
			if(user.get().getCourseList() == null) {
				user.get().setCourseList(new ArrayList<Course>());
			}
			List<Course> list = user.get().getCourseList();
			for(int i=0; i <list.size(); i++) {
				if(list.get(i).getId() == courseId) {
					response.setMessage("The course already added to the user's list");
					return response;
				}
			}
			user.get().getCourseList().add(course.get());
			userRepository.save(user.get());
			response.setMessage("The course is added to the user's list");
			return response;
		}
		response.setMessage("There is no course with this id");
		return response;
	}


	public List<User> getInstructorForOneDepartment(Long departmentId) {
		Optional<Department> department = departmentRepository.findById(departmentId);
		if(department.isEmpty()) {
			return new ArrayList<User>();
		}
		return userRepository.findByTypeAndDepartment("instructor", department.get());
	}


	public Response assignInstructor(Long courseId, Long instructorId) {
		Response response = new Response();
		java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
		DateInformation startDate  = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation finishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		if(startDate != null && !startDate.getDate().after(sqlDate)) {
			if(finishDate != null && finishDate.getDate().after(sqlDate)) {
				Optional<Course> course = courseRepository.findById(courseId);
				if(course.isPresent()) {
					if(course.get().getInstructorId() != 0L) {
						response.setMessage("There is a instructor for this course");
						return response;
					}
					Optional<User> user = userRepository.findByIdAndType(instructorId, "instructor");
					if(user.isPresent()) {
						course.get().setInstructorId(instructorId);
						user.get().getCourseList().add(course.get());
						response.setMessage("The course is added to the instructor's list");
						userRepository.save(user.get());
						courseRepository.save(course.get());
						return response;
					}
					response.setMessage("There is no instructor with this id");
					return response;
				}else {
					response.setMessage("There is no course with this id");
				}
			}else {
				response.setMessage("The semester finished");
			}
		}else {
			response.setMessage("The semester has not started");
		}
		return response;
	}

	public Response deleteCourseInstructor(Long courseId) {
		Response response = new Response();
		java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
		DateInformation startDate  = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation finishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		if(startDate != null && startDate.getDate().before(sqlDate)) {
			if(finishDate != null && finishDate.getDate().after(sqlDate)) {
				Optional<Course> course = courseRepository.findById(courseId);
				if(course.isPresent()) {
					Optional<User> user = userRepository.findByIdAndType(course.get().getInstructorId(), "instructor");
					if(user.isPresent()) {
						List<Course> list = user.get().getCourseList();
						for(int i=0; i < list.size(); i++) {
							if(list.get(i).getId() == courseId) {
								list.remove(i);
								user.get().setCourseList(list);
								response.setMessage("The course is deleted from the instructor's list");
								userRepository.save(user.get());
								course.get().setInstructorId(0L);
								courseRepository.save(course.get());
								List<Form> forms = formRepository.findByUserIdAndCourseIdAndSent(user.get().getId(), courseId, false);
								for(int k=0; k < forms.size(); k++){
									formRepository.deleteById(forms.get(i).getId());
								}
								return response;
							}
						}
					}else {
						response.setMessage("There is no instructor for this course");
						return response;
					}
				}else {
					response.setMessage("There is no course with this id");
				}
			}else {
				response.setMessage("The semester finished");
			}
		}else {
			response.setMessage("The semester has not started");
		}
		return response;
	}
	

	public List<Course> getCoursesForOneUser(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isPresent()) {
			return user.get().getCourseList();
		}
		return null;
	}


	public List<Course> getCoursesForOneDepartment(Long departmentId) {
		List<Course> courses = courseRepository.findByDepartmentId(departmentId);
		return courses;
	}


	public Response addCourseForStudent(Long courseId, Long userId) {
		Response response = new Response();
		java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
		DateInformation startDate  = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation finishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		if(startDate != null && startDate.getDate().before(sqlDate)) {
			if(finishDate != null && finishDate.getDate().after(sqlDate)) {
				Optional<Course> course = courseRepository.findById(courseId);
				if(course.isPresent()) {
					Optional<User> user = userRepository.findByIdAndType(userId, "student");
					if(user.isPresent()) {
						List<Course> list = user.get().getCourseList();
						for(int i=0; i < list.size(); i++) {
							if(list.get(i).getId() == courseId) {
								response.setMessage("The course is already added to the student's course list");
								return response;
							}
						}
						user.get().getCourseList().add(course.get());
						response.setMessage("The course is added to the student's list");
						userRepository.save(user.get());
						courseRepository.save(course.get());
						return response;
					}
					response.setMessage("There is no student with this id");
					return response;
				}else {
					response.setMessage("There is no course with this id");
				}
			}else {
				response.setMessage("The semester finished");
			}
		}else {
			response.setMessage("The semester has not started");
		}
		return response;
	}


	public Response deleteCourseForStudent(Long courseId, Long userId) {
		Response response = new Response();
		Optional<Course> course = courseRepository.findById(courseId);
		if(course.isPresent()) {
			Optional<User> user = userRepository.findByIdAndType(userId, "student");
			if(user.isPresent()) {
				List<Course> list = user.get().getCourseList();
				for(int i=0; i < list.size(); i++) {
					if(list.get(i).getId() == courseId) {
						list.remove(i);
						user.get().setCourseList(list);
						response.setMessage("The course is deleted from the student's list");
						userRepository.save(user.get());
						courseRepository.save(course.get());
						return response;
					}
				}
				response.setMessage("The student don't have this course");
				return response;
			}
			response.setMessage("There is no student with this id");
			return response;
		}
		response.setMessage("There is no course with this id");
		return response;
	}


	public Response forgetPassword(String mail) {
		Response response = new Response();
		Optional<User> user = userRepository.findByMail(mail);
		if(user.isEmpty()) {
			response.setMessage("There is no account with this mail");
			return response;
		}
		String password = String.valueOf((Math.random()));
		user.get().setPassword(passwordEncoder.encode(password));
		userRepository.save(user.get());
		response.setUserId(user.get().getId());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fatih.ay.616161@gmail.com");
        message.setTo(mail);
        message.setText("Your account's new password is: " + String.valueOf(password));
        message.setSubject("New password");
        mailSender.send(message);
		response.setMessage("Your password is changed, check your mail");
		return response;
	}


	public Response sentMailToAdmin(String mail, String message) {
		Response response = new Response();
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mail);
        msg.setTo("fatih.ay.616161@gmail.com");
        msg.setText(message);
        msg.setSubject("Customer Service");
        mailSender.send(msg);
		response.setMessage("The message is sent");
		return response;
	}


	public Response removeBan(Long userId) {
		Response response = new Response();
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			response.setMessage("There is no account with this id");
			return response;
		}	
		if(!user.get().getBan()) {
			response.setMessage("The user is not banned");
			return response;
		}			
		user.get().setBan(false);
		userRepository.save(user.get());
		response.setMessage("Ban removed from user");
		return response;
	}


	public List<String> questionsFromDM(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return null;
		}
		List<User> departmentManagers = userRepository.findByTypeAndDepartment("DM", user.get().getDepartment());
		if(departmentManagers.size() == 0) {
			return null;
		}
		return departmentManagers.get(0).getQuestions();
	}


	public Response addQuestionToResource(Long userId, String question) {
		Response response = new Response();
		Optional<User> user = userRepository.findByIdAndType(userId, "DM");
		if(user.isEmpty()) {
			response.setMessage("There is no user with this id or the user with this id is not department manager");
			return response;
		}
		user.get().getQuestions().add(question);
		userRepository.save(user.get());
		response.setMessage("The question is added");
		return response;
	}


	public Response changeProfilePicture(MultipartFile image, Long userId) throws IOException {
		Response response = new Response();
		Optional<User> check = userRepository.findById(userId);
		if(check.isEmpty()) {
			response.setMessage("There is no user with this id");
			return response;
		}
		User user = check.get();
		user.setImageData(compressImage(image.getBytes()));
		userRepository.save(user);
		response.setMessage("Your profile picture is changed");
		return response;
	}

	public byte[] getProfilePicture(Long userId) {
		Optional<User> check = userRepository.findById(userId);
		if(check.isEmpty()) {
			return null;
		}
		User user = check.get();
        byte[] image = decompressImage(user.getImageData());
        return image;
	}
	
    private byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        return outputStream.toByteArray();
    }

    private byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
        }
        return outputStream.toByteArray();
    }


	public List<Course> getAllCoursesForOneDepartment(Long departmentId, Long studentId) {
		List<Course> courses = courseRepository.findByDepartmentId(departmentId);
		List<Course> result = new ArrayList<Course>();
		for(int i=0; i < courses.size(); i++) {
			if(userRepository.findByCourseAndId(courses.get(i).getId(), studentId).isEmpty()) {
				result.add(courses.get(i));
			}
		}
		return result;
	}
	

}
