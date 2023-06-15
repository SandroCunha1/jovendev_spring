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

import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.services.CountryService;

@RestController
@RequestMapping("/countrys")
public class CountryResource {

	@Autowired
	private CountryService service;
	
	@PostMapping
	public ResponseEntity<Country> insert(@RequestBody Country country) {
		Country newCountry = service.insert(country);
		return newCountry!= null ? ResponseEntity.ok(newCountry) : ResponseEntity.noContent().build();
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Country> findById(@PathVariable Integer id) {
		Country newCountry = service.findById(id);
		return newCountry!= null ? ResponseEntity.ok(newCountry) : ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public ResponseEntity<List<Country>> listAll() {
		List<Country> newCountry = service.listAll();
		return newCountry.size() > 0 ?  ResponseEntity.ok(newCountry) : ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Country> update(@PathVariable Integer id, @RequestBody Country country) {
		country.setId(id);
		country = service.update(country);
		return country!= null ? ResponseEntity.ok(country) : ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
}
