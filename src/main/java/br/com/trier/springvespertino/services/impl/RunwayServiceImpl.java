package br.com.trier.springvespertino.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Runway;
import br.com.trier.springvespertino.repositories.RunwayRepository;
import br.com.trier.springvespertino.services.RunwayService;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@Service
public class RunwayServiceImpl implements RunwayService{
	
		@Autowired
		private RunwayRepository repository;
		
	private void validateRunway(Runway runway) {
		if(runway.getSize() == null || runway.getSize() <= 0) {
			throw new IntegrityViolation("Tamanho da pista inálido");
		}
	}
		
	@Override
	public Runway findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ObjectNotFound("A pista %s não existe".formatted(id)));
	}

	@Override
	public Runway insert(Runway runway) {
		validateRunway(runway);
		return repository.save(runway);
	}

	@Override
	public List<Runway> listAll() {
		List<Runway> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma pista cadastrado");
		}
		return lista;
	}

	@Override
	public Runway update(Runway runway) {
		findById(runway.getId());
		validateRunway(runway);
		return repository.save(runway);
	}

	@Override
	public void delete(Integer id) {
		repository.delete(findById(id));
		
	}

	@Override
	public List<Runway> findByNameStartsWithIgnoreCase(String name) {
		List<Runway> lista = repository.findByNameStartsWithIgnoreCase(name);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma pista cadastrada com nome: %s".formatted(name));
		}
		return lista;
	}

	@Override
	public List<Runway> findBySizeBetween(Integer sizeIn, Integer sizeFin) {
		List<Runway> lista = repository.findBySizeBetween(sizeIn, sizeFin);
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma pista cadastrada com tamanho entre %s e %s".formatted(sizeIn, sizeFin));
		}
		return lista;
	}

	@Override
	public List<Runway> findByCountryOrderBySizeDesc(Country country) {
		List<Runway> lista = repository.findAll();
		if(lista.isEmpty()) {
			throw new ObjectNotFound("Nenhuma pista cadastrada com o país: %s".formatted(country));
		}
		return lista;
	}

}
