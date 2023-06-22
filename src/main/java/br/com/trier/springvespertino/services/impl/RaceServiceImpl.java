package br.com.trier.springvespertino.services.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.models.Runway;
import br.com.trier.springvespertino.repositories.RaceRepository;
import br.com.trier.springvespertino.services.RaceService;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class RaceServiceImpl implements RaceService {

	DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	@Autowired
	private RaceRepository repository;
	
	@Autowired
	private ChampionshipServiceImpl championshipService;
	
	private void validDate(Race race) {
		Championship championship = championshipService.findById(race.getChampionship().getId()) ;
	    if (championship == null || championship.getYear() == null || championship.getYear() != race.getDate().getYear()) {
	        throw new IntegrityViolation("Ano da corrida: %s Deve ser o mesmo ano do campeonato: %s ".formatted(race.getDate().getYear(), championship.getYear()));
	    }
	}
	
	@Override
	public Race findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ObjectNotFound("A corrida %s não existe".formatted(id)));
	}

	@Override
	public Race insert(Race race) {
		validDate(race);
		return repository.save(race);
	}

	@Override
	public List<Race> listAll() {
		List<Race> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma corrida cadastrada");
		}
		return lista;
	}

	@Override
	public Race update(Race race) {
		findById(race.getId());
		validDate(race);
		return repository.save(race);
	}

	@Override
	public void delete(Integer id) {
		repository.delete(findById(id));
		
	}

	@Override
	public List<Race> findByDate(String date) {
		ZonedDateTime zonedDate = ZonedDateTime.parse(date, formatter);
		List<Race> lista = repository.findByDate(zonedDate);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma corrida cadastrada com data %s".formatted(date));
		}
		return lista;
	}

	@Override
	public List<Race> findByDateBetween(String date1, String date2) {
		ZonedDateTime zonedDate1 = ZonedDateTime.parse(date1, formatter);
		ZonedDateTime zonedDate2 = ZonedDateTime.parse(date2, formatter);
		List<Race> lista = repository.findByDateBetween(zonedDate1, zonedDate2);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma corrida cadastrada com data entre %s e %s".formatted(date1, date2));
		}
		return lista;
	}

	@Override
	public List<Race> findByChampionshipOrderByDate(Championship championship) {
		List<Race> lista = repository.findByChampionshipOrderByDate(championship);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma corrida cadastrada no campeonato %s".formatted(championship.getDescription()));
		}
		return lista;
	}

	@Override
	public List<Race> findByRunwayOrderByDate(Runway runway) {
		List<Race> lista = repository.findByRunwayOrderByDate(runway);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma corrida cadastrada na pista %s".formatted(runway.getName()));
		}
		return lista;
	}

}
