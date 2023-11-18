package Group4.Services;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import Group4.Entities.Course;
import Group4.Entities.DateInformation;
import Group4.Entities.Form;
import Group4.Entities.Question;
import Group4.Entities.User;
import Group4.Repositories.CourseRepository;
import Group4.Repositories.DateInformationRepository;
import Group4.Repositories.FormRepository;
import Group4.Repositories.QuestionRepository;
import Group4.Repositories.UserRepository;
import Group4.Requests.NewFormRequest;
import Group4.Requests.DateRequest;
import Group4.Requests.MultipleChoiceRequest;
import Group4.Requests.OpenEndedRequest;
import Group4.Responses.Response;

@Service
public class FormService {

	@Autowired
	private FormRepository formRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private DateInformationRepository dateInformationRepository;
	
    @Autowired
    private JavaMailSender mailSender;
    
	@Autowired
	private QuestionRepository questionRepository;
	
	public List<Form> getAllForms() {
		return formRepository.findAll();
	}
	
	public List<Form> getAllSentFormsFromTeachers() {
		List<Form> forms = formRepository.findAllBySent(true);
		List<Form> finalList = new ArrayList<Form>(); 
		if(forms.size() == 0) {
			return null;
		}
		for (int i=0; i < forms.size(); i++) {
			if(forms.get(i).getUser().getType().equals("instructor")) {
				finalList.add(forms.get(i));
			}
		}
		return finalList;
	}
	public Response saveOneForm(NewFormRequest newForm) {
		Response response = new Response();
		
		Optional<User> user = userRepository.findById(newForm.getUserId());
		if(!user.isPresent()) {
			response.setMessage("There is not an user with this id");
			return response;
		}
		
		if(!user.get().getType().equals("instructor")) {
			response.setMessage("This Id is not belonging to an instructor");
			return response;
		}
		
		Optional<Course> course = courseRepository.findById(newForm.getCourseId());
		if(!course.isPresent()) {
			response.setMessage("There is not a course with this id");
			return response;
		}
		Optional<Form> form = formRepository.findByUserIdAndCourseId(newForm.getUserId(),newForm.getCourseId());
		if(form.isPresent()) {
			response.setMessage("There is a form for this course");
			return response;
		}
		Form toSave = new Form();
		toSave.setCourse(course.get());
		toSave.setCourseName(course.get().getName());
		toSave.setSent(false);
		toSave.setDoItLater(0);
		toSave.setUser(user.get());
		List<Question> list = new ArrayList<Question>();
		list.add(getMultipleChoiceQuestion("The Instructor satisfactorily responded to questions"));
		list.add(getMultipleChoiceQuestion("Communication with the Instructor was adequate"));
		list.add(getMultipleChoiceQuestion("The Instructor assessed course progress by questioning or using other appropriate means"));
		list.add(getMultipleChoiceQuestion("The communication of information maintained my interest in the classroom or online."));
		list.add(getMultipleChoiceQuestion("The Instructor made clear the applications of the subject matter to my major, other courses, or to my life"));
		list.add(getMultipleChoiceQuestion("The materials used to support assignments in the course (texts, readings, websites, were useful."));
		list.add(getMultipleChoiceQuestion("I would rate this Instructor as an effective teacher."));
		list.add(getOpenQuestion("What has been the best aspect of this course?"));
		list.add(getOpenQuestion("Would you change anything about this course/instructor? If so, what would you change?"));
		toSave.setQuestionList(list);
		formRepository.save(toSave);
		response.setMessage("The form is saved");
		return response;
	}

	public Response deleteOneForm(Long id) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(id);
		if(form.isEmpty()) {
			response.setMessage("There is not a form with this id");
			return response;
		}
		Optional<DateInformation> date = dateInformationRepository.findById("evaluation");
		if(date.isPresent()) {
			Date evaluation = date.get().getDate();
			java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
			if(!(evaluation.after(today) && !checkIfItIsSomeDaysBefore(today,evaluation,1))) {
				response.setMessage("Evaluation is scheduled to start in 1 day at the latest, you can no longer make changes");
				return response;
			}
		}
		if(form.get().getDoItLater() < 3) {
			if(!form.get().getSent()) {
				List<Question> list = form.get().getQuestionList();
				for(int i=0; i < list.size(); i++) {
					questionRepository.deleteById(list.get(i).getId());
				}
				formRepository.deleteById(form.get().getId());
				Form toSave = new Form();
				toSave.setCourse(form.get().getCourse());
				toSave.setSent(false);
				toSave.setDoItLater(form.get().getDoItLater());
				toSave.setUser(form.get().getUser());
				List<Question> list2 = new ArrayList<Question>();
				list2.add(getMultipleChoiceQuestion("The Instructor satisfactorily responded to questions"));
				list2.add(getMultipleChoiceQuestion("Communication with the Instructor was adequate"));
				list2.add(getMultipleChoiceQuestion("The Instructor assessed course progress by questioning or using other appropriate means"));
				list2.add(getMultipleChoiceQuestion("The communication of information maintained my interest in the classroom or online."));
				list2.add(getMultipleChoiceQuestion("The Instructor made clear the applications of the subject matter to my major, other courses, or to my life"));
				list2.add(getMultipleChoiceQuestion("The materials used to support assignments in the course (texts, readings, websites, were useful."));
				list2.add(getMultipleChoiceQuestion("I would rate this Instructor as an effective teacher."));
				list2.add(getOpenQuestion("What has been the best aspect of this course?"));
				list2.add(getOpenQuestion("Would you change anything about this course/instructor? If so, what would you change?"));
				toSave.setQuestionList(list2);
				formRepository.save(toSave);
				response.setMessage("The form is reseted");
			}else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You can not delete this form, you reached the update limit for this course");
		}

		return response;
	}

	public Response deleteOneQuestionFromForm(Long id, Long questionId) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(id);
		Form toDelete = form.get();
		if(toDelete.getDoItLater() < 3) {
			if(!toDelete.getSent()) {
				List<Question> list = toDelete.getQuestionList();
				int i = -1;
				for(int a = 0; a < list.size(); a++) {
					if(list.get(a).getId() == questionId) {
						i = a;
					}
				}
				if(i == -1) {
					response.setMessage("There is no question with this id");
					return response;
				}
				if(i <= 6) {
					response.setMessage("You can not delete this question");
					return response;
				}
				list.remove(i);
				response.setMessage("The question is deleted");
				questionRepository.deleteById(questionId);
				toDelete.setQuestionList(list);
				formRepository.save(toDelete);
			} else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You have reached the save limit, you need to send");
		}
		return response;
	}

	public Response addOpenEndedQuestionToForm(Long id, OpenEndedRequest question) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(id);
		Form toUpdate = form.get();
		if(toUpdate.getDoItLater() < 3) {
			if(!toUpdate.getSent()) {
				Question newQuestion = new Question();
				newQuestion.setType("openEnded");
				newQuestion.setFinalAnswer(0);
				newQuestion.setAnswer("");
				newQuestion.setQuestion(question.getQuestion());
				toUpdate.getQuestionList().add(newQuestion);
				questionRepository.save(newQuestion);
				formRepository.save(toUpdate);
				response.setMessage("The question is added");
			}else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You have reached the save limit, you need to send");
		}
		return response;
	}

	public Response addMultipleQuestionToForm(Long id, MultipleChoiceRequest question) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(id);
		Form toUpdate = form.get();
		if(toUpdate.getDoItLater() < 3) {
			if(!toUpdate.getSent()) {
				Question newQuestion = new Question();
				newQuestion.setType("multipleChoice");
				newQuestion.setFinalAnswer(0);
				newQuestion.setAnswer("");
				newQuestion.setQuestion(question.getQuestion());
				List<String> list =  new ArrayList<String>();
				list.add(question.getAnswer1());
				list.add(question.getAnswer2());
				list.add(question.getAnswer3());
				list.add(question.getAnswer4());
				list.add(question.getAnswer5());
				newQuestion.setAnswerList(list);
				toUpdate.getQuestionList().add(newQuestion);
				questionRepository.save(newQuestion);
				formRepository.save(toUpdate);
				response.setMessage("The question is added");
			}else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You have reached the save limit, you need to send");
		}
		return response;
	}
	

	public List<Form> getAllFormForUser(Long studentId) {
		return formRepository.findByUserId(studentId);
	}


	public Form getFormForInstructor(Long formId) {
		Optional<Form> form = formRepository.findById(formId);
		if(form.isPresent()) {
			return form.get();
		}
		return null;
	}
	
	private Question getMultipleChoiceQuestion(String message) {
		List<String> answerList =  new ArrayList<String>();
		answerList.add("1");
		answerList.add("2");
		answerList.add("3");
		answerList.add("4");
		answerList.add("5");
		Question question = new Question();
		question.setType("multipleChoice");
		question.setQuestion(message);
		question.setAnswerList(answerList);	
		questionRepository.save(question);
		return question;
		}
	
	private Question getOpenQuestion(String message) {
		Question question = new Question();
		question.setType("openEnded");
		question.setQuestion(message);
		questionRepository.save(question);
		return question;
		}

	public Response sendFormForOneCourse(Long formId) {
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation semesterFinishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		DateInformation evaluation = dateInformationRepository.findById("evaluation").orElse(null);
		if(semesterFinishDate.getDate().after(today)) {
			Optional<Form> check = formRepository.findById(formId);
			if(check.isEmpty()) {
				response.setMessage("There is no form with this id");
				return response;
			}
			Form form = check.get();
			if(form.getStartDate() == null || form.getFinishDate() == null) {
				response.setMessage("You need to set the start date and end date for the form");
				return response;
			}
			if(form.getFinishDate().after(semesterFinishDate.getDate())) {
				response.setMessage("The finish date must be before the semester finish date");
				return response;
			}
			if(!form.getFinishDate().after(evaluation.getDate())) {
				response.setMessage("The finish date must be after the evaluation start date");
				return response;
			}
			if(!form.getSent()) {
				form.setSent(true);
				Course course = form.getCourse();
				List<User> students = userRepository.findByCourse(course.getId());
				List<Question> questions = form.getQuestionList();
				for(int i=0; i < students.size(); i++) {
					if(students.get(i).getId() != form.getUser().getId()) {
						Form newForm = new Form();
						newForm.setCourse(course);
						newForm.setCourseName(course.getName());
						newForm.setSent(false);
						newForm.setDoItLater(0);
						newForm.setFinishDate(form.getFinishDate());
						newForm.setStartDate(form.getStartDate());
						newForm.setUser(students.get(i));
						List<Question> newQuestions = new ArrayList<Question>();
						for(int k=0; k < questions.size(); k++) {
							Question question = new Question();
							question.setAnswer("");
							List<String> answerList = questions.get(k).getAnswerList();
							List<String> newAnswerList = new ArrayList<String>();
							for(int l=0; l < answerList.size(); l++) {
								newAnswerList.add(answerList.get(l));
							}
							question.setAnswerList(newAnswerList);
							question.setFinalAnswer(0);
							question.setQuestion(questions.get(k).getQuestion());
							question.setType(questions.get(k).getType());
							questionRepository.save(question);
							newQuestions.add(question);
						}
						newForm.setQuestionList(newQuestions);
						formRepository.save(newForm);
					}
				}
				response.setMessage("The course's form sent to students");
			}else {
				response.setMessage("The course's form was already sent to students");
			}
		}else {
			response.setMessage("The semester finished");
		}
		return response;
	}

	public Response saveOneFormForStudent(Form form) {
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation evaluationDate = dateInformationRepository.findById("evaluation").orElse(null);
		if(today.before(evaluationDate.getDate())) {
			response.setMessage("Evaluation is not started");
			return response;
		}
		if(form.getDoItLater() < 2) {
			if(!form.getSent()) {
				form.setDoItLater(form.getDoItLater() + 1);
				formRepository.save(form);
				response.setMessage("The form is saved");
			} else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You have reached the save limit, you need to send");
		}
		return response;
	}

	public Response sentOneFormForStudent(Form form) {
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation evaluationDate = dateInformationRepository.findById("evaluation").orElse(null);
		if(today.before(evaluationDate.getDate())) {
			response.setMessage("Evaluation is not started");
			return response;
		}
		Course course = form.getCourse();
		Long departmentNumber = course.getDepartment().getId() * 100000;
		Long realCourseId = course.getId() - departmentNumber;
		if(realCourseId >= 50000) {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 7) {
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}else {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 13){
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}
		if(today.after(form.getFinishDate())) {
			response.setMessage("You can no longer submit the form");
			return response;
		}
		if(!form.getSent()) {
			form.setSent(true);
			formRepository.save(form);
			String name = "";
			String surname = "";
			List<User> users = userRepository.findByCourse(course.getId());
			for(int i=0; i < users.size(); i++) {
				if(users.get(i).getType().equals("instructor")) {
					name = users.get(i).getName();
					surname = users.get(i).getSurname();
				}
			}
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("fatih.ay.616161@gmail.com");
	        message.setTo(form.getUser().getMail());
	        message.setText("You have successfully given feedback for the lesson given by " + name + " " + surname + ", coded " + String.valueOf(course.getId()));
	        message.setSubject("Certificate of Completion");
	        mailSender.send(message);
			response.setMessage("The form is sent");
		} else {
			response.setMessage("The form was already sent");
		}
		return response;
	}

	public List<Form> getAllAnswerFormForOneCourse(Long courseId) {
		java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
		DateInformation date = dateInformationRepository.findById("detailedResults").orElse(null);
		if(date != null && !date.getDate().after(sqlDate)) {
			List<Form> forms = formRepository.findByCourseIdAndSent(courseId, true);
			for(int i = 0; i < forms.size(); i++){
				if(forms.get(i).getUser().getType().equals("instructor")) {
					forms.remove(i);
					break;
				}
			}
			return forms;
		}
		return new ArrayList<Form>();
	}

	public List<Float> getStatisticalDataForOneCourse(Long courseId) {
		List<Form> forms = formRepository.findByCourseIdAndSent(courseId, true);
		List<Integer> average = new ArrayList<Integer>();
		List<Float> result = new ArrayList<Float>();
		int lock = 0;
		int index = 0;
		float size = 0;
		for (int i=0; i < forms.size(); i++) {
			Form form = forms.get(i);
			if(!form.getUser().getType().equalsIgnoreCase("instructor")) {
				size += 1.0;
				List<Question> questions = form.getQuestionList();
				index = 0;
				for (int k=0; k < questions.size(); k++) {
					Question question =  questions.get(k);
					if(question.getType().equals("multipleChoice") && question.getFinalAnswer() != 0) {
						if(lock == 1) {
							average.add(index, (average.get(index) + question.getFinalAnswer()));
						}else {
							average.add(question.getFinalAnswer());
						}
						index += 1;
					}
				}
				lock = 1;
			}
		}
		for (int i=0; i < average.size(); i++) {
			result.add((average.get(i)/size));
		}
		return result;
	}

	public Response updateNumberOfSaves(Long formId) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(formId);
		if(form.isEmpty()) {
			response.setMessage("There is no form with this id");
			return response;
		}
		form.get().setDoItLater(form.get().getDoItLater() + 1);
		formRepository.save(form.get());
		response.setMessage("Number of saves is updated");
		return response;
	}

	public Response getNumberOfSaves(Long formId) {
		Response response = new Response();
		Optional<Form> form = formRepository.findById(formId);
		if(form.isEmpty()) {
			response.setMessage("There is no form with this id");
			return response;
		}
		response.setMessage("Number of saves so far for this form: " + String.valueOf(form.get().getDoItLater()));
		return response;
	}

	public boolean checkFormIfSend(Long formId) {
		Optional<Form> form = formRepository.findById(formId);
		if(form.isEmpty()) {
			return false;
		}
		return form.get().getSent();
	}

	public Response setDatesOfForm(Long formId, DateRequest dates) {
		Response response = new Response();
		Optional<Form> check = formRepository.findById(formId);
		if(check.isEmpty()) {
			response.setMessage("There is no form with this id");
			return response;
		}
		Date startDate = Date.valueOf(dates.getDate());
		Date finishDate = Date.valueOf(dates.getDate2());
		Date date = new Date(System.currentTimeMillis());
		if(startDate.before(date)) {
			response.setMessage("Instructors cannot change the form's start date before the evaluation start date");
			return response;
		}
		Form form = check.get();
		if(form.getStartDate() != null && form.getSent() && startDate.before(form.getStartDate())) {
			response.setMessage("You cannot change the start date to before of the old start day");
			return response;
		}
		java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
		if(finishDate.before(sqlDate)) {
			response.setMessage("You cannot change the finish date to past one");
			return response;
		}
		DateInformation semesterFinishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		DateInformation semesterStartDate = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation evaluation = dateInformationRepository.findById("evaluation").orElse(null);
		if(evaluation == null || semesterStartDate == null || semesterFinishDate == null) {
			response.setMessage("In order to do this, first the admin has to enter the information of the critical dates");
			return response;
		}
		if(startDate.before(semesterStartDate.getDate())) {
			response.setMessage("The start date must be after the semester start date");
			return response;
		}
		if(finishDate.after(semesterFinishDate.getDate())) {
			response.setMessage("The finish date must be before the semester finish date");
			return response;
		}
		if(!finishDate.after(evaluation.getDate())) {
			response.setMessage("The finish date must be after the evaluation start date");
			return response;
		}
		if(!startDate.before(finishDate)) {
			response.setMessage("Start date need to be before finish date");
			return response;
		}
		form.setStartDate(startDate);
		form.setFinishDate(finishDate);
		formRepository.save(form);
		response.setMessage("The dates saved");
		return response;
	}

	public Float percentOfCompletion(Long courseId) {
		List<Form> allFormsForCourse = formRepository.findByCourseId(courseId);
		List<Form> sentFormsForCourse = formRepository.findByCourseIdAndSent(courseId, true);
		int sent = sentFormsForCourse.size();
		int all = allFormsForCourse.size();
		if(sent == 0 || sent == 1) {
			return (float)0;
		}
		return ((float)sent / (float)all) * 100;
	}

	public Response sendRegularMails() throws MessagingException {
		Response response = new Response();
		DateInformation semesterFinishDate = dateInformationRepository.findById("semesterFinish").orElse(null);
		DateInformation semesterStartDate = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation evaluationDate  = dateInformationRepository.findById("evaluation").orElse(null);
		DateInformation detailedResultsDate  = dateInformationRepository.findById("detailedResults").orElse(null);
		if(semesterFinishDate == null || semesterStartDate == null || evaluationDate == null || detailedResultsDate == null) {
			response.setMessage("Enter the dates that need to be entered first");
			return response;
		}
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		List<User> students = userRepository.findByType("student");
		int numberOfStudents = students.size();
		SimpleMailMessage message = new SimpleMailMessage();
		if(checkIfItIsSomeDaysBefore(today, evaluationDate.getDate(), 0)) {
			for(int i=0; i < numberOfStudents; i++) {
		        message.setFrom("fatih.ay.616161@gmail.com");
		        message.setTo(students.get(i).getMail());
		        message.setText("The evaluation is starting today");
		        message.setSubject("Notification about evaluation");
		        mailSender.send(message);
			}
		}
		if(checkIfItIsSomeDaysBefore(today, evaluationDate.getDate(), 2)) {
			for(int i=0; i < numberOfStudents; i++) {
		        message.setFrom("fatih.ay.616161@gmail.com");
		        message.setTo(students.get(i).getMail());
		        message.setText("2 days until evaluation starts");
		        message.setSubject("Notification about evaluation");
		        mailSender.send(message);
			}
		}
		List<Form> forms = formRepository.findByUserType("student");
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		//change link
		for(int i=0; i < forms.size(); i++) {
			Form form = forms.get(i);
			if(checkIfItIsSomeDaysBefore(today, form.getStartDate(), 0)) {
				helper.setFrom("fatih.ay.616161@gmail.com");
				helper.setTo(form.getUser().getMail());
		        String html="<a href=\"https://www.w3schools.com/\">Go to form</a>";
		        helper.setText(html, true);
		        helper.setSubject("Today, you can evaluate the form of " + form.getCourse().getName());
		        mailSender.send(mimeMessage);
			}
			if(checkIfItIsSomeDaysBefore(today, form.getFinishDate(), 2)) {
				helper.setFrom("fatih.ay.616161@gmail.com");
				helper.setTo(form.getUser().getMail());
		        String html="<a href=\"https://www.w3schools.com/\">Go to form</a>";
		        helper.setText(html, true);
		        helper.setSubject("Today, 2 days left to complete the form of " + form.getCourse().getName());
		        mailSender.send(mimeMessage);
			}else if(checkIfItIsSomeDaysBefore(today, form.getFinishDate(), 4)) {
				helper.setFrom("fatih.ay.616161@gmail.com");
				helper.setTo(form.getUser().getMail());
		        String html="<a href=\"https://www.w3schools.com/\">Go to form</a>";
		        helper.setText(html, true);
		        helper.setSubject("Today, 4 days left to complete the form of " + form.getCourse().getName());
		        mailSender.send(mimeMessage);
			}
		}
		response.setMessage("The job is done");
		return response;
	}
	
	private Boolean checkIfItIsSomeDaysBefore(Date before, Date after, int howManyDays) {
		Calendar beforeDate = Calendar.getInstance();
		Calendar afterDate = Calendar.getInstance();
		beforeDate.setTime(before);
		afterDate.setTime(after);
		return (((beforeDate.get(Calendar.YEAR) == afterDate.get(Calendar.YEAR)) && ((afterDate.get(Calendar.DAY_OF_YEAR) - beforeDate.get(Calendar.DAY_OF_YEAR)) == howManyDays)) || 
				(((afterDate.get(Calendar.YEAR) - beforeDate.get(Calendar.YEAR)) == 1) 
						&& ((31 - beforeDate.get(Calendar.DAY_OF_MONTH) + afterDate.get(Calendar.DAY_OF_MONTH)) == howManyDays)));
	}

	public Response getExcelFileForOneResult(Long formId) {
		Response response = new Response();
		Optional<Form> check = formRepository.findById(formId);
		if(check.isEmpty()) {
			response.setMessage("There is no form with this id");
			return response;
		}
		String currentPath = System.getProperty("user.dir");
		
		response.setMessage("The result donwloaded to " + currentPath);
		return response;
	}

	public List<Course> getAllCoursesWithoutForms(Long instructorId) {
		Optional<User> user = userRepository.findById(instructorId);
		if(user.isEmpty()) {
			return null;
		}
		List<Course> courses = user.get().getCourseList();
		List<Course> result = new ArrayList<Course>();
		for(int i = 0; i < courses.size(); i++) {
			if(!formRepository.findByUserIdAndCourseId(instructorId, courses.get(i).getId()).isPresent()) {
				result.add(courses.get(i));
			}
		}
		return result;
	}

	public List<Form> getAllOpenFormForUser(Long userId) {
		List<Form> forms =  formRepository.findByUserIdAndSent(userId, false);
		/*List<Form> result = new ArrayList<Form>();
		if(forms.size() > 0) {
			User user = userRepository.findById(userId).orElse(null);
			String type = user.getType();
			for(int i=0; i < forms.size(); i++) {
				if(type.equals("instructor")) {
					if(forms.get(i).getDoItLater() < 3) {
						result.add(forms.get(i));
					}
				}else {
					if(forms.get(i).getDoItLater() < 2) {
						result.add(forms.get(i));
					}
				}
			}
		}*/

		return forms;
	}

	public List<Form> getAllSentFormForUser(Long instructorId) {
		return formRepository.findByUserIdAndSent(instructorId, true);
	}

	public Response saveOneFormForStudent(List<Question> questions, Long formId) {
		Response response = new Response();
		Optional<Form> check = formRepository.findById(formId);
		if(check.isEmpty()) {
			response.setMessage("Thre is no form with this id");
			return response;
		}
		Form form = check.get();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation evaluationDate = dateInformationRepository.findById("evaluation").orElse(null);
		if(today.before(evaluationDate.getDate())) {
			response.setMessage("Evaluation is not started");
			return response;
		}
		if(form.getDoItLater() < 2) {
			if(!form.getSent()) {
				form.setDoItLater(form.getDoItLater() + 1);
				form.setQuestionList(questions);
				for(int i=0; i < questions.size(); i++){
					questionRepository.save(questions.get(i));
				}
				formRepository.save(form);
				response.setMessage("The form is saved");
			} else {
				response.setMessage("The form was already sent");
			}
		}else {
			response.setMessage("You have reached the save limit, you need to send");
		}
		return response;
	}

	public Response sentOneFormForStudent(List<Question> questions, Long formId) {
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation evaluationDate = dateInformationRepository.findById("evaluation").orElse(null);
		if(today.before(evaluationDate.getDate())) {
			response.setMessage("Evaluation is not started");
			return response;
		}
		Optional<Form> check = formRepository.findById(formId);
		if(check.isEmpty()) {
			response.setMessage("Thre is no form with this id");
			return response;
		}
		Form form = check.get();
		Course course = form.getCourse();
		Long departmentNumber = course.getDepartment().getId() * 100000;
		Long realCourseId = course.getId() - departmentNumber;
		if(realCourseId >= 50000) {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 7) {
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}else {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 13){
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}
		if(today.after(form.getFinishDate())) {
			response.setMessage("You can no longer submit the form");
			return response;
		}
		if(!form.getSent()) {
			form.setSent(true);
			for(int i=0; i < questions.size(); i++) {
				questionRepository.save(questions.get(i));
			}
			form.setQuestionList(questions);
			formRepository.save(form);
			String name = "";
			String surname = "";
			List<User> users = userRepository.findByCourse(course.getId());
			for(int i=0; i < users.size(); i++) {
				if(users.get(i).getType().equals("instructor")) {
					name = users.get(i).getName();
					surname = users.get(i).getSurname();
				}
			}
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("fatih.ay.616161@gmail.com");
	        message.setTo(form.getUser().getMail());
	        message.setText("You have successfully given feedback for the lesson given by " + name + " " + surname + ", coded " + String.valueOf(course.getId()));
	        message.setSubject("Certificate of Completion");
	        mailSender.send(message);
			response.setMessage("The form is sent");
		} else {
			response.setMessage("The form was already sent");
		}
		return response;
	}

	public Response saveMultipleChoiceQuestion(Long finalAnswer, Long formId, Long questionId) {
		Question question = questionRepository.findById(questionId).orElse(null);
		question.setFinalAnswer(Long.valueOf(finalAnswer).intValue());
		Response response = new Response();
		questionRepository.save(question);
		Form form = formRepository.findById(formId).orElse(null);
		List<Question> list = form.getQuestionList();
		for(int i=0; i < list.size() ; i++) {
			if(list.get(i).getId() == questionId) {
				list.set(i, question);
			}
		}
		formRepository.save(form);
		response.setMessage("The question is saved");
		return response;

	}

	public Response saveOpenEndedQuestion(String answer, Long formId, Long questionId) {
		Question question = questionRepository.findById(questionId).orElse(null);
		Response response = new Response();
		question.setAnswer(answer);
		questionRepository.save(question);
		Form form = formRepository.findById(formId).orElse(null);
		List<Question> list = form.getQuestionList();
		for(int i=0; i < list.size() ; i++) {
			if(list.get(i).getId() == questionId) {
				list.set(i, question);
			}
		}
		formRepository.save(form);
		response.setMessage("The question is saved");
		return response;
		
	}

	public Response sentOneFormForStudent(Long formId) {
		Form form = formRepository.findById(formId).orElse(null);
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		DateInformation evaluationDate = dateInformationRepository.findById("evaluation").orElse(null);
		if(today.before(evaluationDate.getDate())) {
			response.setMessage("Evaluation is not started");
			return response;
		}
		Course course = form.getCourse();
		Long departmentNumber = course.getDepartment().getId() * 100000;
		Long realCourseId = course.getId() - departmentNumber;
		if(realCourseId >= 50000) {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 7) {
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}else {
			if((userRepository.findByCourse(realCourseId).size() - 1) < 13){
				response.setMessage("This course is a small course, not enough student takes this course");
				return response;
			}
		}
		if(today.after(form.getFinishDate())) {
			response.setMessage("You can no longer submit the form");
			return response;
		}
		if(!form.getSent()) {
			form.setSent(true);
			formRepository.save(form);
			String name = "";
			String surname = "";
			List<User> users = userRepository.findByCourse(course.getId());
			for(int i=0; i < users.size(); i++) {
				if(users.get(i).getType().equals("instructor")) {
					name = users.get(i).getName();
					surname = users.get(i).getSurname();
				}
			}
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("fatih.ay.616161@gmail.com");
	        message.setTo(form.getUser().getMail());
	        message.setText("You have successfully given feedback for the lesson given by " + name + " " + surname + ", coded " + String.valueOf(course.getId()));
	        message.setSubject("Certificate of Completion");
	        mailSender.send(message);
			response.setMessage("The form is sent");
		} else {
			response.setMessage("The form was already sent");
		}
		return response;
	}

}
