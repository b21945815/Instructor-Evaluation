package Group4.Entities;


import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="question")
@Data
public class Question {
	
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long id;
	private String type;

	//For open ended questions
	@Lob
	@Column(columnDefinition="text")
	private String answer;
	private String question;	
	
	//For multiple choice questions
	private Integer finalAnswer;
    @ElementCollection 
    @CollectionTable(name = "answer_list", joinColumns = @JoinColumn(name = "id")) 
    @Column(name = "answer") 
	private List<String> answerList;
}
