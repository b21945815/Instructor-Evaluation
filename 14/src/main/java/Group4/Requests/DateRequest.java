package Group4.Requests;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.NonNull;

public class DateRequest {
	
	@NonNull
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate date;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate date2;
	public @NonNull LocalDate getDate() {
		return date;
	}
	public @NonNull LocalDate getDate2() {
		return date2;
	}
}
