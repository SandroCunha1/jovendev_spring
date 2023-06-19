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
import br.com.trier.springvespertino.models.User;
import br.com.trier.springvespertino.services.UserService;


@RestController
@RequestMapping("/users")
public class UserResource {
	@Autowired
	private UserService service;
	
	@PostMapping
	public ResponseEntity<User> insert(@RequestBody User user) {
		return ResponseEntity.ok(service.insert(user));
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<User> findById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}
	
	@GetMapping
	public ResponseEntity<List<User>> listAll() {
		return ResponseEntity.ok(service.listAll());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<User>> findByNameStartsWithIgnoreCase(@PathVariable String name) {
		return ResponseEntity.ok(service.findByNameStartsWithIgnoreCase(name));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User user) {
		user.setId(id);
		return ResponseEntity.ok(service.update(user));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	
	
}
