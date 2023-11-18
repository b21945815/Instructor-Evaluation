package Group4.Entities;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="request")
@Data
public class Request {
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	private String message;
	private Long senderId;
	private Long suspectId;
}
