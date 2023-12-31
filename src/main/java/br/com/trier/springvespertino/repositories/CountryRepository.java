package br.com.trier.springvespertino.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.trier.springvespertino.models.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
	
	List<Country> findAllByOrderByName();
	List<Country> findByNameStartsWithIgnoreCase(String name);
	Country findByName(String name);
}
