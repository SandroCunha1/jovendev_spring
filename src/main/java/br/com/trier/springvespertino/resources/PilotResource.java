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
import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.services.CountryService;
import br.com.trier.springvespertino.services.PilotService;
import br.com.trier.springvespertino.services.TeamService;

@RestController
@RequestMapping("/pilots")
public class PilotResource {


	@Autowired
	private PilotService service;
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private TeamService teamService;
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Pilot> findById(@PathVariable Integer id){
		return ResponseEntity.ok(service.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<Pilot> insert(@RequestBody Pilot pilot){
		countryService.findById(pilot.getCountry().getId());
		teamService.findById(pilot.getTeam().getId());
		return ResponseEntity.ok(service.insert(pilot));
	}
	
	@GetMapping
	public ResponseEntity<List<Pilot>> listAll(){
		return ResponseEntity.ok(service.listAll());
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<Pilot> update (@RequestBody Pilot pilot, @PathVariable Integer id){
		countryService.findById(pilot.getCountry().getId());
		teamService.findById(pilot.getTeam().getId());
		pilot.setId(id);
		return ResponseEntity.ok(service.update(pilot));
		
	}
	
	@DeleteMapping
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Pilot>> findByNameStartsWithIgnoreCase(@PathVariable String name){
		return ResponseEntity.ok(service.findByNameStartsWithIgnoreCase(name));
	}
	
	@GetMapping("/country/{id}")
	public ResponseEntity<List<Pilot>> findByCountryOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByCountryOrderByName(countryService.findById(id)));
	}
	
	@GetMapping("/team/{id}")
	public ResponseEntity<List<Pilot>> findByTeamOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByTeamOrderByName(teamService.findById(id)));
	}
	
	
}
