package br.com.trier.springvespertino.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.models.PilotRace;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.repositories.PilotRaceRepository;
import br.com.trier.springvespertino.services.PilotRaceService;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class PilotRaceServiceImpl implements PilotRaceService {
	
	@Autowired
	private PilotRaceRepository repository;

	@Override
	public PilotRace findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ObjectNotFound("O relacionamento piloto/corrida %s não existe".formatted(id)));
	}

	@Override
	public PilotRace insert(PilotRace pilotRace) {
		return repository.save(pilotRace);
	}

	@Override
	public List<PilotRace> listAll() {
		List<PilotRace> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum piloto/corrida cadastrado");
		}
		return lista;
	}

	@Override
	public PilotRace update(PilotRace pilotRace) {
		findById(pilotRace.getId());
		return repository.save(pilotRace);
	}

	@Override
	public void delete(Integer id) {
		repository.delete(findById(id));
		
	}

	@Override
	public List<PilotRace> findByPilotOrderByPlacement(Pilot pilot) {
		List<PilotRace> lista = repository.findByPilotOrderByPlacement(pilot);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Piloto não cadastrado : %s".formatted(pilot.getName()));
		}
		return lista;
	}

	@Override
	public List<PilotRace> findByRaceOrderByPlacement(Race race) {
		List<PilotRace> lista = repository.findByRaceOrderByPlacement(race);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum piloto associado a corrida de id: %s".formatted(race.getId()));
		}
		return lista;
	}

	@Override
	public List<PilotRace> findByPlacement(Integer placement) {
		List<PilotRace> lista = repository.findByPlacement(placement);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum piloto cadastrado com colocação: %s".formatted(placement));
		}
		return lista;
	}

}
