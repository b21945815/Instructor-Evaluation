package Group4.Requests;

import lombok.Data;
import lombok.NonNull;


@Data
public class NewCourseRequest {
	
	@NonNull
	private Long id;
	@NonNull
	private String name;
	@NonNull
	private Long credit;
	@NonNull
	private String type;
	@NonNull
	private Long quota;	
	@NonNull
	private String department;
}