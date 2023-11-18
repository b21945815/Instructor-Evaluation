package Group4.Requests;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequest {

	@NonNull
	private String password;
	@NonNull
	private String mail;

}
