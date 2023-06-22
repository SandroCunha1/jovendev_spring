package br.com.trier.springvespertino.models;

import java.time.ZonedDateTime;

import br.com.trier.springvespertino.models.dto.RaceDTO;
import br.com.trier.springvespertino.utils.DateUtils;
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
	@Column(name = "id_race")
	private Integer id;
	
	@Column(name = "date")
	private ZonedDateTime date;
	
	@ManyToOne
	@NotNull
	private Runway runway;
	
	@ManyToOne
	@NotNull
	private Championship championship;
	
	public Race(RaceDTO raceDTO, Runway runway, Championship championship) {
		this(raceDTO.getId(), DateUtils.dateBrToZoneDate(raceDTO.getDate()), runway, championship);			
	}
	
	public RaceDTO toDTO() {
		return new RaceDTO(id, DateUtils.zoneDateToBrDate(date), runway.getId(), runway.getName(), championship.getId(), championship.getDescription());
	}
	
}
