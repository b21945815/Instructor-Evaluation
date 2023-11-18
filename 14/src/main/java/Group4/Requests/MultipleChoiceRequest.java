package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class MultipleChoiceRequest {


	@NonNull
	private String question;
	@NonNull
	private String answer1;
	@NonNull
	private String answer2;
	@NonNull
	private String answer3;	
	@NonNull
	private String answer4;
	@NonNull
	private String answer5;
}
