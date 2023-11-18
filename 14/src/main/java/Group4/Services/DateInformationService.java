package Group4.Services;

import java.sql.Date;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.DateInformation;
import Group4.Repositories.DateInformationRepository;
import Group4.Requests.DateRequest;
import Group4.Responses.Response;

@Service
public class DateInformationService {

	@Autowired
	private DateInformationRepository dateInformationRepository;
	
	public Response setSemesterStartDate(DateRequest dateRequest) {
		Date date = Date.valueOf(dateRequest.getDate());
		Response response = new Response();
		Optional<DateInformation> oldStartDate  = dateInformationRepository.findById("semesterStart");
		Optional<DateInformation> oldFinishDate  = dateInformationRepository.findById("semesterFinish");
		Calendar Date = Calendar.getInstance();
		Calendar compareDate = Calendar.getInstance();
		Date.setTime(date);
		if(oldFinishDate.isEmpty()) {
			DateInformation newDate = new DateInformation();
			newDate.setName("semesterStart");
			newDate.setDate(date);
			dateInformationRepository.save(newDate);
			response.setMessage("The date is saved");
			return response;
		}
		compareDate.setTime(oldFinishDate.get().getDate());
		if((date.after(oldFinishDate.get().getDate()) || 
				((Date.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR)) && ((compareDate.get(Calendar.DAY_OF_YEAR) == Date.get(Calendar.DAY_OF_YEAR)))))) {
			response.setMessage("Semester Start date can't be later than semester final day");
			return response;
		}
		if(oldStartDate.isEmpty()) {
			DateInformation newDate = new DateInformation();
			newDate.setName("semesterStart");
			newDate.setDate(date);
			dateInformationRepository.save(newDate);
		}else {
			oldStartDate.get().setDate(date);
			dateInformationRepository.save(oldStartDate.get());	
		}	
		response.setMessage("The date is saved");
		return response;
	}
	
	public Response setSemesterFinishDate(DateRequest dateRequest) {
		Date date = Date.valueOf(dateRequest.getDate());
		Response response = new Response();
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		if(!date.after(today)) {
			response.setMessage("Semester final date need to be after today");
			return response;
		}
		Optional<DateInformation> oldStartDate  = dateInformationRepository.findById("semesterStart");
		Optional<DateInformation> oldFinishDate  = dateInformationRepository.findById("semesterFinish");
		Calendar Date = Calendar.getInstance();
		Calendar compareDate = Calendar.getInstance();
		Date.setTime(date);
		if(oldStartDate.isEmpty()) {
			DateInformation newDate = new DateInformation();
			newDate.setName("semesterFinish");
			newDate.setDate(date);
			dateInformationRepository.save(newDate);
			response.setMessage("The date is saved");
			return response;
		}
		compareDate.setTime(oldStartDate.get().getDate());
		if(oldStartDate.isPresent() && (date.before(oldStartDate.get().getDate()) ||
				((Date.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR)) && ((compareDate.get(Calendar.DAY_OF_YEAR) == Date.get(Calendar.DAY_OF_YEAR)))))) {
			response.setMessage("Semester final date can't be before than semester start day");
			return response;
		}
		if(oldFinishDate.isEmpty()) {
			DateInformation newDate = new DateInformation();
			newDate.setName("semesterFinish");
			newDate.setDate(date);
			dateInformationRepository.save(newDate);
		}else {
			oldFinishDate.get().setDate(date);
			dateInformationRepository.save(oldFinishDate.get());
		}	
		response.setMessage("The date is saved");
		return response;
	}
	
	public Response setDetailedResultsDate(DateRequest dateRequest) {
		Date date = Date.valueOf(dateRequest.getDate());
		Response response = new Response();
		DateInformation oldStartDate  = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation oldFinishDate  = dateInformationRepository.findById("semesterFinish").orElse(null);
		DateInformation evaluationDate  = dateInformationRepository.findById("evaluation").orElse(null);
		if(evaluationDate != null && evaluationDate.getDate().before(date)) {
			if(date.after(oldStartDate.getDate()) && date.before(oldFinishDate.getDate())) {
				DateInformation newDate = new DateInformation();
				newDate.setName("detailedResults");
				newDate.setDate(date);
				dateInformationRepository.save(newDate);
				response.setMessage("The date is saved");
				return response;
			}
		}else {
			response.setMessage("Don't forget that evaluation day need to be before detailed results day");
			return response;
		}
		response.setMessage("Enter a new date taking into account the semester dates");
		return response;
	}
	
	public Response setEvaluationDate(DateRequest dateRequest) {
		Date date = Date.valueOf(dateRequest.getDate());
		Response response = new Response();
		DateInformation oldStartDate  = dateInformationRepository.findById("semesterStart").orElse(null);
		DateInformation oldFinishDate  = dateInformationRepository.findById("semesterFinish").orElse(null);
		DateInformation detailedResultsDate  = dateInformationRepository.findById("detailedResults").orElse(null);
		if(oldStartDate == null || oldFinishDate == null) {
			response.setMessage("First you have to enter the semester start and end dates");
			return response;
		}
		if(detailedResultsDate == null || detailedResultsDate.getDate().after(date)) {
			if(date.after(oldStartDate.getDate()) && date.before(oldFinishDate.getDate())) {
				DateInformation newDate = new DateInformation();
				newDate.setName("evaluation");
				newDate.setDate(date);
				dateInformationRepository.save(newDate);
				response.setMessage("The date is saved");
				return response;
			}
		}else {
			response.setMessage("The evaluation start date need to be before the detailed Results date");
			return response;
		}
		response.setMessage("Enter a new date taking into account the semester dates");
		return response;
	}
	
	public DateInformation getSemesterStartDate() {
		DateInformation date  = dateInformationRepository.findById("semesterStart").orElse(null);
		return date;
	}
	
	public DateInformation getSemesterFinishDate() {
		DateInformation date = dateInformationRepository.findById("semesterFinish").orElse(null);
		return date;
	}

	public DateInformation getDetailedResultsDate() {
		DateInformation date  = dateInformationRepository.findById("detailedResults").orElse(null);
		return date;
	}

	public DateInformation getEvaluationDate() {
		DateInformation date  = dateInformationRepository.findById("evaluation").orElse(null);
		return date;
	}
}
