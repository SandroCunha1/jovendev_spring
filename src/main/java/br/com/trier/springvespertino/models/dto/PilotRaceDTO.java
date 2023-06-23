package br.com.trier.springvespertino.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PilotRaceDTO {

	private Integer id;
	private Integer placement;
	private Integer pilotId;
	private String pilotName;
	private Integer raceId;
	private String raceDate;
}
