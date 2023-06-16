package br.com.trier.springvespertino.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Championship;

public interface ChampionshipRepository extends JpaRepository<Championship, Integer> {
	    List<Championship> findByYear(Integer year);
	    List<Championship> findByDescriptionContainingIgnoreCase(String description);
}
