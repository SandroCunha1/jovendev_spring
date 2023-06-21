package br.com.trier.springvespertino.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.repositories.CountryRepository;
import br.com.trier.springvespertino.services.CountryService;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	private CountryRepository repository;
	
	private void findByName(Country country) {
		Country busca = repository.findByName(country.getName());
		if(busca != null && busca.getId() != country.getId()) {
			throw new IntegrityViolation("Nome já cadastrado : %s".formatted(country.getName()));
		}
	}
	
	@Override
	public Country findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> 
		new ObjectNotFound("O país %s não existe".formatted(id)));
	}

	@Override
	public Country insert(Country country) {
		
		findByName(country);
		return repository.save(country);
	}

	@Override
	public List<Country> listAll() {
		List<Country> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum país cadastrado");
		}
		return lista;
	}

	@Override
	public Country update(Country country) {
		findById(country.getId());
		findByName(country);
		return repository.save(country);
	}

	@Override
	public void delete(Integer id) {
		Country country = findById(id);
		repository.delete(country);
		
	}

	@Override
	public List<Country> findByNameStartsWithIgnoreCase(String name) {
		List<Country> lista = repository.findByNameStartsWithIgnoreCase(name);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhum país inicia com %s".formatted(name));
		}
		return lista;
	}

	@Override
	public List<Country> findAllByOrderByName() {
		repository.findAll();
		return repository.findAllByOrderByName();
	}
}
