package Group4.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Data;

@Entity
@Table(name="resources")
@Data
public class Resource {
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;
	private String message;
	private String link;
	
	@ManyToOne
	@JoinColumn(name = "department_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private Department department;
}
