package Group4.Entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="studentRegisterRequest")
@Data
public class StudentRegisterRequest {

	@Id
	@Column(nullable = false, updatable = false)
	private Long id;
	
	@Column(nullable = false, updatable = false)
	private String mail;
	
	@Column(nullable = false, updatable = false)
	private String department;
}
