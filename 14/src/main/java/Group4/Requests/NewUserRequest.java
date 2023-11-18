package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class NewUserRequest {
	
	@NonNull
	private Long id;
	@NonNull
	private String mail;
	@NonNull
	private String type;
	@NonNull
	private String department;
}
