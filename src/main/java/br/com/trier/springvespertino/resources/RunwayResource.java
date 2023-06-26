package br.com.trier.springvespertino.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.trier.springvespertino.models.Runway;
import br.com.trier.springvespertino.services.CountryService;
import br.com.trier.springvespertino.services.RunwayService;

@RestController
@RequestMapping("/runways")
public class RunwayResource {

	@Autowired
	private RunwayService service;
	
	@Autowired
	private CountryService countryService;
	
	@Secured({"ROLE_USER"})
	@GetMapping("/{id}")
	public ResponseEntity<Runway> findById(@PathVariable Integer id){
		return ResponseEntity.ok(service.findById(id));
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping
	public ResponseEntity<Runway> insert(@RequestBody Runway runway){
		countryService.findById(runway.getCountry().getId());
		return ResponseEntity.ok(service.insert(runway));
	}
	
	@Secured({"ROLE_USER"})
	@GetMapping
	public ResponseEntity<List<Runway>> listAll(){
		return ResponseEntity.ok(service.listAll());
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/{id}")
	public ResponseEntity<Runway> update (@RequestBody Runway runway, @PathVariable Integer id){
		countryService.findById(runway.getCountry().getId());
		runway.setId(id);
		return ResponseEntity.ok(service.update(runway));
		
	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@Secured({"ROLE_USER"})
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Runway>> findByNameStartsWithIgnoreCase(@PathVariable String name){
		return ResponseEntity.ok(service.findByNameStartsWithIgnoreCase(name));
		
	}
	
	@Secured({"ROLE_USER"})
	@GetMapping("/size/{sizeIn}/{sizeFin}")
	public ResponseEntity<List<Runway>> findBySizeBetween(@PathVariable Integer sizeIn, @PathVariable Integer sizeFin){
		return ResponseEntity.ok(service.findBySizeBetween(sizeIn, sizeFin));
	}
	
	@Secured({"ROLE_USER"})
	@GetMapping("/country/{idPais}")
	public ResponseEntity<List<Runway>> findByCountryOrderBySizeDesc(@PathVariable Integer idPais){
		return ResponseEntity.ok(service.findByCountryOrderBySizeDesc(countryService.findById(idPais)));
	}
	
	
	
}
