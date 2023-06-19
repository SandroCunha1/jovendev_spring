package br.com.trier.springvespertino.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.repositories.ChampionshipRepository;
import br.com.trier.springvespertino.services.ChampionshipService;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class ChampionshipServiceImpl implements ChampionshipService {

	@Autowired
	private ChampionshipRepository repository;
	
	@Override
	public Championship findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> 
		new ObjectNotFound("O campeonato %s não existe".formatted(id)));
	}

	@Override
	public Championship insert(Championship championship) {
		validYear(championship.getYear());
		return repository.save(championship);
	}

	@Override
	public List<Championship> listAll() {
		List<Championship> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum campeonato cadastrado");
		}
		return lista;
	}

	@Override
	public Championship update(Championship championship) {
		validYear(championship.getYear());
		return repository.save(championship);
	}

	@Override
	public void delete(Integer id) {
		repository.delete(findById(id));
	}
	
	private void validYear(Integer year) {
		if(year < 1990 || year > LocalDate.now().plusYears(1).getYear()) {
			throw new IntegrityViolation("O campeonato deve estar ente 1990 e %s".formatted(LocalDate.now().plusYears(1).getYear()));
		}
	}

	@Override
	public List<Championship> findByYear(Integer year) {
		List<Championship> lista = repository.findByYear(year);
		validYear(year);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum campeonato em %s".formatted(year));
		}
		return repository.findByYear(year);
	}

	@Override
	public List<Championship> findByDescriptionContaining(String description) {
		List<Championship> lista = repository.findByDescriptionContainingIgnoreCase(description);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum campeonato contem %s".formatted(description));
		}
		return lista;
	}

	@Override
	public List<Championship> findByYearBetween(Integer num1, Integer num2) {
		List<Championship> lista = repository.findByYearBetween(Math.min(num1,num2), Math.max(num1, num2));
		validYear(num1);
		validYear(num2);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum campeonato entre %s e %s".formatted(Math.min(num1,num2), Math.max(num1, num2)));
		}
		return lista;
	}
	
	
}
