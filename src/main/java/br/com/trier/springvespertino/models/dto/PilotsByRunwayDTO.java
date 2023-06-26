package br.com.trier.springvespertino.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PilotsByRunwayDTO {

	private String runwayName;
	private Integer quantity;
	private List<PilotDTO> pilots;
}