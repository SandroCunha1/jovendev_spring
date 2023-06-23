package br.com.trier.springvespertino.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.trier.springvespertino.models.PilotRace;
import br.com.trier.springvespertino.models.dto.PilotRaceDTO;
import br.com.trier.springvespertino.services.PilotRaceService;
import br.com.trier.springvespertino.services.PilotService;
import br.com.trier.springvespertino.services.RaceService;

@RestController
@RequestMapping("/pilots/races")
public class PilotRaceResource {



	@Autowired
	private PilotRaceService service;
	
	@Autowired
	private PilotService pilotService;
	
	@Autowired
	private RaceService raceService;
	
	
	@GetMapping("/{id}")
	public ResponseEntity<PilotRaceDTO> findById(@PathVariable Integer id){
		return ResponseEntity.ok(service.findById(id).toDTO());
	}
	
	@PostMapping
	public ResponseEntity<PilotRaceDTO> insert(@RequestBody PilotRaceDTO pilotRaceDTO){
		PilotRace pilotR = new PilotRace(pilotRaceDTO, pilotService.findById(pilotRaceDTO.getPilotId()), raceService.findById(pilotRaceDTO.getRaceId())); 
		return ResponseEntity.ok(service.insert(pilotR).toDTO());
	}
	
	@GetMapping
	public ResponseEntity<List<PilotRaceDTO>> listAll(){
		return ResponseEntity.ok(service.listAll()
				.stream()
				.map(pilotR -> pilotR.toDTO())
				.toList());
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<PilotRaceDTO> update (@RequestBody PilotRaceDTO pilotRaceDTO, @PathVariable Integer id){
		PilotRace pilotR = new PilotRace(pilotRaceDTO, pilotService.findById(pilotRaceDTO.getPilotId()), raceService.findById(pilotRaceDTO.getRaceId())); 
		pilotR.setId(id);
		return ResponseEntity.ok(service.update(pilotR).toDTO());
		
	}
	
	@DeleteMapping
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/placement/{placement}")
	public ResponseEntity<List<PilotRaceDTO>> findByPlacement(@PathVariable Integer placement){
		return ResponseEntity.ok(service.findByPlacement(placement)
				.stream()
				.map(pilot -> pilot.toDTO())
				.toList());
	}
	
	@GetMapping("/pilot/{id}")
	public ResponseEntity<List<PilotRaceDTO>> findByPilotOrderByPlacement(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByPilotOrderByPlacement(pilotService.findById(id))
				.stream()
				.map(pilot -> pilot.toDTO())
				.toList());
	}
	
	@GetMapping("/race/{id}")
	public ResponseEntity<List<PilotRaceDTO>> findByRaceOrderByPlacement(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByRaceOrderByPlacement(raceService.findById(id))
				.stream()
				.map(pilot -> pilot.toDTO())
				.toList());
	}
	
}
