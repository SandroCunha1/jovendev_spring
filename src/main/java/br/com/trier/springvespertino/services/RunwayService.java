package br.com.trier.springvespertino.services;

import java.util.List;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Runway;

public interface RunwayService {

	Runway findById(Integer id);
	
	Runway insert(Runway runway);
	
	List<Runway> listAll();
	
	Runway update (Runway runway);
	
	void delete(Integer id);
	
	List<Runway> findByNameStartsWithIgnoreCase(String name);
	
	List<Runway> findBySizeBetween(Integer sizeIn, Integer sizeFin);
	
	List<Runway> findByCountryOrderBySizeDesc(Country country);
}
