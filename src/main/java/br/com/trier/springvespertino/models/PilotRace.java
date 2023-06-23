package br.com.trier.springvespertino.models;

import br.com.trier.springvespertino.models.dto.PilotRaceDTO;
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
@Entity(name="pilot_race")
public class PilotRace {

	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_p_r")
	private Integer id;
	
	@Column(name = "placement")
	private Integer placement;
	
	@ManyToOne
	@NotNull
	private Pilot pilot;
	
	@ManyToOne
	@NotNull
	private Race race;
	
	public PilotRace(PilotRaceDTO pilotRaceDTO, Pilot pilot, Race race) {
		this(pilotRaceDTO.getId(), pilotRaceDTO.getPlacement(), pilot, race);	
	}
	
	public PilotRaceDTO toDTO() {
		return new PilotRaceDTO(id, placement, pilot.getId(), pilot.getName(), race.getId(), DateUtils.zoneDateToBrDate(race.getDate()));
	}
	
}
