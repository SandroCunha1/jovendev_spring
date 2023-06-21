package br.com.trier.springvespertino.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.models.Team;

public interface PilotRepository extends JpaRepository<Pilot, Integer>{
	
	List<Pilot> findByNameStartsWithIgnoreCase(String name);
	List<Pilot> findByCountryOrderByName(Country country);
	List<Pilot> findByTeamOrderByName(Team team);
	
}
