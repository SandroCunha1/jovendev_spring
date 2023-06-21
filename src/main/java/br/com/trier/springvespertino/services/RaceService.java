package br.com.trier.springvespertino.services;

import java.util.List;

import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.models.Runway;

public interface RaceService {
	
	Race findById(Integer id);
	
	Race insert(Race race);
	
	List<Race> listAll();
	
	Race update (Race race);
	
	void delete(Integer id);
	
	List<Race> findByDate(String date);
	
	List<Race> findByDateBetween(String date1, String date2);
	
	List<Race> findByRunwayOrderByDate(Runway runway);
	
	List<Race> findByChampionshipOrderByDate(Championship championship);
	
}
