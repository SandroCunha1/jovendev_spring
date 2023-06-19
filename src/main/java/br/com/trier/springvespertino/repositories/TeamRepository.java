package br.com.trier.springvespertino.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.trier.springvespertino.models.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {

	List<Team> findByNameStartsWithIgnoreCase(String name);
	
	List<Team> findAllByOrderByName();
	
	Team findByName(String name);
}
