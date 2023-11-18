package Group4.Requests;

import lombok.Data;
import lombok.NonNull;


@Data
public class NewResourceRequest {


	@NonNull
	private String title;
	@NonNull
	private Long departmentId;
	@NonNull
	private String message;
	@NonNull
	private String link;
}
