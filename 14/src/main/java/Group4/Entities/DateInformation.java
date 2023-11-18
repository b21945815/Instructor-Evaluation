package Group4.Entities;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Data;

@Entity
@Table(name="date")
@Data
public class DateInformation {
	
	@Id
	@Column(nullable = false, updatable = false)
	private String name;
	
	private Date date;
}
