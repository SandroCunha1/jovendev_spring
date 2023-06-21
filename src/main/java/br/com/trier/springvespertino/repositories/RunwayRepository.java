package br.com.trier.springvespertino.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Runway;

public interface RunwayRepository extends JpaRepository<Runway, Integer> {

	List<Runway> findByNameStartsWithIgnoreCase(String name);
	List<Runway> findBySizeBetween(Integer sizeIn, Integer sizeFin);
	List<Runway> findByCountryOrderBySizeDesc(Country country);
}
