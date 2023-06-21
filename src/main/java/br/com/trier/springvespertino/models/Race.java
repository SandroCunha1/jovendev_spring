package br.com.trier.springvespertino.models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name="race")
public class Race {
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_runway")
	private Integer id;
	
	@Column(name = "date")
	private ZonedDateTime date;
	
	@ManyToOne
	@NotNull
	private Runway runway;
	
	@ManyToOne
	@NotNull
	private Championship championship;
	
	public Race(String zonedDateTime, Runway runway, Championship championship) {
		this.runway = runway;
		this.championship = championship;
		DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		ZonedDateTime zonedDate = ZonedDateTime.parse(zonedDateTime, formatter);
		this.date = zonedDate;
		
	}
}
