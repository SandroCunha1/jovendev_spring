package br.com.trier.springvespertino.resources;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.services.TeamService;

@RestController
@RequestMapping("/teams")
public class TeamResource {

	@Autowired
	private TeamService service;
	
	@PostMapping
	public ResponseEntity<Team> insert(@RequestBody Team team) {
		return ResponseEntity.ok(service.insert(team));
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Team> findById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}
	
	@GetMapping
	public ResponseEntity<List<Team>> listAll() {
		return ResponseEntity.ok(service.listAll());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Team>> findByNameStartsWithIgnoreCase(@PathVariable String name) {
		return ResponseEntity.ok(service.findByNameStartsWithIgnoreCase(name));
	}
	
	@GetMapping("/name")
	public ResponseEntity<List<Team>> findAllByOrderByName() {
		return ResponseEntity.ok(service.findAllByOrderByName());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Team> update(@PathVariable Integer id, @RequestBody Team team) {
		team.setId(id);
		return ResponseEntity.ok(service.update(team));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
}
