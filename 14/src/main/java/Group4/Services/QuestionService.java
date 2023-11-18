package Group4.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.Question;
import Group4.Repositories.QuestionRepository;

@Service
public class QuestionService {
	
	public static final String[] TYPES = new String[] {"multipleChoice","openEnded"};
	
	@Autowired
	private QuestionRepository questionRepository;
	

	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}



}
