package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class NewFormRequest {

	@NonNull
	private Long userId;
	@NonNull
	private Long courseId;
}
