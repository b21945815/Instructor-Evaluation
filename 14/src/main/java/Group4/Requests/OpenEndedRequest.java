package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class OpenEndedRequest {


	@NonNull
	private String question;
	@NonNull
	private String dummy;
}
