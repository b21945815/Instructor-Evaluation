package Group4.Entities;

import java.sql.Date;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Entity
@Table(name="form")
@Data
public class Form {
	
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	
	private int doItLater;
	private Boolean sent;
	private Date startDate;
	private Date finishDate;
	private String courseName;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_id", nullable=true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Question> questionList;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User user;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Course course;
	
}
