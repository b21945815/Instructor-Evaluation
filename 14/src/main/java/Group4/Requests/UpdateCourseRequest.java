package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateCourseRequest {
	
	@NonNull
	private Long id;
	@NonNull
	private String name;

	private Long credit;

	private Long quota;	
}
