package Group4.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Group4.Entities.Request;
import Group4.Entities.User;
import Group4.Repositories.RequestRepository;
import Group4.Repositories.UserRepository;
import Group4.Responses.Response;

@Service
public class RequestService {

	@Autowired
	private RequestRepository requestRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Response banRequest(Long senderId, Long suspectId, String message) {
		Response response = new Response();
		Request request = new Request();
		request.setSenderId(senderId);
		request.setSuspectId(suspectId);
		request.setMessage(message);
		requestRepository.save(request);
		response.setMessage("The ban request sent");
		return response;
	}

	public List<Request> getBanRequests() {
		return requestRepository.findAll();
	}

	public Response acceptBanRequest(Long requestId) {
		Response response = new Response();
		Optional<Request> request = requestRepository.findById(requestId);
		if(request.isEmpty()) {
			response.setMessage("There is no ban request with this id");
			return response;
		}
		Optional<User> user = userRepository.findById(request.get().getSuspectId());
		user.get().setBan(true);
		userRepository.save(user.get());
		requestRepository.deleteById(requestId);
		response.setMessage("The user is banned");
		return response;
	}


}
