package br.com.trier.springvespertino.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PilotsTeamAndChampionshipDTO {
	private String teamName;
	private String championshipName;
	private Integer championshipDate;
	private List<PilotDTO> pilots;
}