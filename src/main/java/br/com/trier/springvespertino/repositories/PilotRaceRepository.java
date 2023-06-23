package br.com.trier.springvespertino.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.models.PilotRace;
import br.com.trier.springvespertino.models.Race;

public interface PilotRaceRepository extends JpaRepository<PilotRace, Integer> {

	List<PilotRace> findByPilotOrderByPlacement(Pilot piloto);
	List<PilotRace> findByRaceOrderByPlacement(Race race);
	List<PilotRace> findByPlacement(Integer placement);
}
