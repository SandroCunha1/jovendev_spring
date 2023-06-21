package br.com.trier.springvespertino.repositories;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.models.Runway;

public interface RaceRepository extends JpaRepository<Race, Integer> {
	List<Race> findByDate(ZonedDateTime date);
	List<Race> findByDateBetween(ZonedDateTime date1, ZonedDateTime date2);
	List<Race> findByChampionshipOrderByDate(Championship championship);
	List<Race> findByRunwayOrderByDate(Runway runway);
}
