package br.com.trier.springvespertino.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.repositories.TeamRepository;
import br.com.trier.springvespertino.services.TeamService;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamRepository repository;
	
	private void findByName(Team team) {
		Team busca = repository.findByName(team.getName());
		if(busca != null && busca.getId() != team.getId()) {
			throw new IntegrityViolation("Nome já cadastrado : %s".formatted(team.getName()));
		}
	}
	
	@Override
	public Team findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> 
		new ObjectNotFound("A equipe %s não existe".formatted(id)));
	}

	@Override
	public Team insert(Team team) {
		findByName(team);
		return repository.save(team);
	}

	@Override
	public List<Team> listAll() {
		List<Team> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma equipe cadastrada");
		}
		return lista;
	}

	@Override
	public Team update(Team team) {
		findById(team.getId());
		findByName(team);		
		return repository.save(team);
	}

	@Override
	public void delete(Integer id) {
		Team team = findById(id);
		repository.delete(team);
		
	}

	@Override
	public List<Team> findByNameStartsWithIgnoreCase(String name) {
		List<Team> lista = repository.findByNameStartsWithIgnoreCase(name);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma equipe inicia com %s".formatted(name));
		}
		return lista;
	}

	@Override
	public List<Team> findAllByOrderByName() {
		repository.findAll();
		return repository.findAllByOrderByName();
	}
}
