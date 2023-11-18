package Group4.Requests;




import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateUserRequest {
	
	@NonNull
	private Long id;

	private String name;

	private String surname;
	@NonNull
	private String password;

	private String phone;

	private String mail;
}
