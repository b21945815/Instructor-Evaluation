package Group4.Controllers;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import Group4.Entities.StudentRegisterRequest;
import Group4.Entities.User;
import Group4.Requests.LoginRequest;
import Group4.Responses.Response;
import Group4.Security.JwtTokenProvider;
import Group4.Services.StudentRegisterRequestService;
import Group4.Services.UserService;


@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private StudentRegisterRequestService studentRegisterRequestService;
	
	
	/*
	 * LoginRequest:
	 * 	{
       		"mail": "Fatih@gmail",
    		"password": "123"
	   	}
		Returns response which have login token if your information correct*/
	//If login is correct message == type
	@PostMapping("/login")
	public Response login(@RequestBody LoginRequest loginRequest) {
		Response response = new Response();
		Optional<User> user = userService.getOneUserByMail(loginRequest.getMail());
		if(user.isEmpty()) {
			response.setMessage("There is not any user with this mail");
			return response;
		}
		if(user.get().getBan()) {
			response.setMessage("You are banned");
			return response;
		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getMail(), loginRequest.getPassword());
		try {
			Authentication auth = authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(auth);
			String jwtToken = jwtTokenProvider.generateJwtToken(auth);
			response.setMessage(user.get().getType());
			response.setAccessToken("Bearer " + jwtToken);
			response.setUserId(user.get().getId());
		}catch(Exception e){
			response.setMessage("You entered wrong password");
		}

		return response;
	}
	/*
	 *This is for student register request*/
	@PostMapping("/register")
	public Response register(@RequestBody StudentRegisterRequest registerRequest) {
		
		return studentRegisterRequestService.saveRequest(registerRequest);
	}
	/*
	 *When you use this function, the new password is sent to the given e-mail.*/
	@PostMapping("/forgetPassword/{mail}")
	public Response forgetPassword(@PathVariable String mail){
		return userService.forgetPassword(mail);
	}
	/*
	 *With this function, someone can send a message to the admin. */
	@PostMapping("/customerService/{mail}/{message}")
	public Response customerService(@PathVariable String mail, @PathVariable String message){
		return userService.sentMailToAdmin(mail, message);
	}
}
