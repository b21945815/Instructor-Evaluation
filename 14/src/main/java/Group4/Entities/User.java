package Group4.Entities;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Entity
@Table(name="user")
@Data
public class User {
	
	//school id
	@Id
	@Column(nullable = false, updatable = false)
	private Long id;
	
	private String name;
	private String surname;
	private String password;
	private String mail;
	private String phone;
	private String type;
	private Boolean ban;
	
    @Lob
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable=true)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private List<Course> courseList;
	
	@ElementCollection
    @CollectionTable(name = "question_list", joinColumns = @JoinColumn(name = "id")) 
    @Column(name = "question") 
	private List<String> questions;
	
	@ManyToOne
	@JoinColumn(name = "department_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private Department department;
}
