package Group4.Entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="course")
@Data
public class Course {
	//departmentId = 01 bbm101 section 01 = 0110101 
	@Id
	@Column(nullable = false, updatable = false)
	private Long id;

	private String name;
	private Long credit;
	private String type;
	private Long quota;	
	private Long instructorId;

	@ManyToOne
	@JoinColumn(name = "department_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Department department;
}
