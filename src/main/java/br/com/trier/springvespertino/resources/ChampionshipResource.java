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
import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.services.ChampionshipService;

@RestController
@RequestMapping("/championships")
public class ChampionshipResource {
	
	@Autowired
	private ChampionshipService service;
	
	@PostMapping
	public ResponseEntity<Championship> insert(@RequestBody Championship championship) {
		Championship newChampionship = service.insert(championship);
		return newChampionship!= null ? ResponseEntity.ok(newChampionship) : ResponseEntity.noContent().build();
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Championship> findById(@PathVariable Integer id) {
		Championship newChampionship = service.findById(id);
		return newChampionship!= null ? ResponseEntity.ok(newChampionship) : ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public ResponseEntity<List<Championship>> listAll() {
		List<Championship> newChampionship = service.listAll();
		return newChampionship.size() > 0 ?  ResponseEntity.ok(newChampionship) : ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Championship> update(@PathVariable Integer id, @RequestBody Championship championship) {
		championship.setId(id);
		championship = service.update(championship);
		return championship!= null ? ResponseEntity.ok(championship) : ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
}
