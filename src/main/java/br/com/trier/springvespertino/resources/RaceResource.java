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
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.services.ChampionshipService;
import br.com.trier.springvespertino.services.RaceService;
import br.com.trier.springvespertino.services.RunwayService;

@RestController
@RequestMapping("/races")
public class RaceResource {

	@Autowired
	private RaceService service;
	
	@Autowired
	private ChampionshipService championshipService;
	
	@Autowired
	private RunwayService runwayService;
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Race> findById(@PathVariable Integer id){
		return ResponseEntity.ok(service.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<Race> insert(@RequestBody Race race){
		championshipService.findById(race.getChampionship().getId());
		runwayService.findById(race.getRunway().getId());
		return ResponseEntity.ok(service.insert(race));
	}
	
	@GetMapping
	public ResponseEntity<List<Race>> listAll(){
		return ResponseEntity.ok(service.listAll());
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<Race> update (@RequestBody Race race, @PathVariable Integer id){
		championshipService.findById(race.getChampionship().getId());
		runwayService.findById(race.getRunway().getId());
		race.setId(id);
		return ResponseEntity.ok(service.update(race));
		
	}
	
	@DeleteMapping
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/date/{date}")
	public ResponseEntity<List<Race>> findByDate(@PathVariable String date){
		return ResponseEntity.ok(service.findByDate(date));
	}
	
	@GetMapping("/date/{date1}/{date2}")
	public ResponseEntity<List<Race>> findByDateBetween(@PathVariable String date1, @PathVariable String date2){
		return ResponseEntity.ok(service.findByDateBetween(date1, date2));
	}
	
	@GetMapping("/runway/{id}")
	public ResponseEntity<List<Race>> findByRunwayOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByRunwayOrderByDate(runwayService.findById(id)));
	}
	
	@GetMapping("/championship/{id}")
	public ResponseEntity<List<Race>> finfByChampionshipOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByChampionshipOrderByDate(championshipService.findById(id)));
	}
	

}
