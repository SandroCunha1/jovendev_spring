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
import br.com.trier.springvespertino.models.dto.RaceDTO;
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
	public ResponseEntity<RaceDTO> findById(@PathVariable Integer id){
		return ResponseEntity.ok(service.findById(id).toDTO());
	}
	
	@PostMapping
	public ResponseEntity<RaceDTO> insert(@RequestBody RaceDTO raceDTO){
		Race race = new Race(raceDTO, runwayService.findById(raceDTO.getChampionshipId()), championshipService.findById(raceDTO.getChampionshipId())); 
		return ResponseEntity.ok(service.insert(race).toDTO());
	}
	
	@GetMapping
	public ResponseEntity<List<RaceDTO>> listAll(){
		return ResponseEntity.ok(service.listAll()
				.stream()
				.map(race -> race.toDTO())
				.toList());
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<RaceDTO> update (@RequestBody RaceDTO raceDTO, @PathVariable Integer id){
		Race race = new Race(raceDTO, runwayService.findById(raceDTO.getChampionshipId()), championshipService.findById(raceDTO.getChampionshipId())); 
		race.setId(id);
		return ResponseEntity.ok(service.update(race).toDTO());
		
	}
	
	@DeleteMapping
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/date/{date}")
	public ResponseEntity<List<RaceDTO>> findByDate(@PathVariable String date){
		return ResponseEntity.ok(service.findByDate(date)
				.stream()
				.map(race -> race.toDTO())
				.toList());
	}
	
	@GetMapping("/date/{date1}/{date2}")
	public ResponseEntity<List<RaceDTO>> findByDateBetween(@PathVariable String date1, @PathVariable String date2){
		return ResponseEntity.ok(service.findByDateBetween(date1, date2)
				.stream()
				.map(race -> race.toDTO())
				.toList());
	}
	
	@GetMapping("/runway/{id}")
	public ResponseEntity<List<RaceDTO>> findByRunwayOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByRunwayOrderByDate(runwayService.findById(id))
				.stream()
				.map(race -> race.toDTO())
				.toList());
	}
	
	@GetMapping("/championship/{id}")
	public ResponseEntity<List<RaceDTO>> finfByChampionshipOrderByName(@PathVariable Integer id){
		return ResponseEntity.ok(service.findByChampionshipOrderByDate(championshipService.findById(id))
				.stream()
				.map(race -> race.toDTO())
				.toList());
	}
	

}
