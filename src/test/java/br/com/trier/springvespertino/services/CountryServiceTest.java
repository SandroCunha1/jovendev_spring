package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
class CountryServiceTest extends BaseTests{


	@Autowired
	CountryService countryService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchIdValid() {
		Country pais = countryService.findById(1);
		assertNotNull(pais);
		assertEquals(1, pais.getId());
		assertEquals("Brasil", pais.getName());	
	}
	
	@Test
	@DisplayName("Buscar por ID inválido")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchIdInvalid() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		countryService.findById(10));
		assertEquals("O país 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchAll() {	
		assertEquals(4, countryService.listAll().size());
	}
	
	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	void searchAllWithNoUser() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		countryService.listAll());
		assertEquals("Nenhum país cadastrado", ex.getMessage());
	}
	
	@Test
	@DisplayName("Insert novo pais")
	void insert() {	
		Country pais = new Country(null, "insert");
		countryService.insert(pais);
		assertEquals(1, countryService.listAll().size());
		assertEquals(1, pais.getId());
		assertEquals("insert", pais.getName());	
	}
	
	@Test
	@DisplayName("Insert novo pais com nome duplicado")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void insertWithSameName() {	
		Country pais = new Country(null, "Irlanda");
		var ex = assertThrows(IntegrityViolation.class, () ->
		countryService.insert(pais));
		assertEquals("Nome já cadastrado : Irlanda", ex.getMessage());
	}
	
	@Test
	@DisplayName("Update pais")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void update() {	
		Country pais = countryService.findById(1);
		assertNotNull(pais);
		assertEquals(1, pais.getId());
		assertEquals("Brasil", pais.getName());	
		pais = new Country(1, "update");
		countryService.update(pais);
		assertEquals(4, countryService.listAll().size());
		assertEquals(1, pais.getId());
		assertEquals("update", pais.getName());	
	}
	
	@Test
	@DisplayName("Update pais não existente")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void updateInvalid() {
		Country pais = new Country(10, "ABC");
		var ex = assertThrows(ObjectNotFound.class, () ->
		countryService.update(pais));
		assertEquals("O país 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Delete usuário")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void deleteCountry() {	
		assertEquals(4, countryService.listAll().size());
		countryService.delete(1);
		assertEquals(3, countryService.listAll().size());
		assertEquals(2, countryService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Delete usuário que não existe")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void deleteCountryNoExist() {	
		assertEquals(4, countryService.listAll().size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		countryService.delete(10));
		assertEquals("O país 10 não existe", ex.getMessage());
		assertEquals(4, countryService.listAll().size());
		assertEquals(1, countryService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por nome")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchByName() {	
		assertEquals(1, countryService.findByNameStartsWithIgnoreCase("br").size());
		assertEquals(2, countryService.findByNameStartsWithIgnoreCase("i").size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		countryService.findByNameStartsWithIgnoreCase("x"));
		assertEquals("Nenhum país inicia com x", ex.getMessage());
		
	}
	
	@Test
	@DisplayName("Ordena por nome")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchAllAndOrderByName() {	
		assertEquals(1, countryService.findAllByOrderByName().get(0).getId());
		assertEquals(2, countryService.findAllByOrderByName().get(1).getId());
		assertEquals(4, countryService.findAllByOrderByName().get(2).getId());
		assertEquals(3, countryService.findAllByOrderByName().get(3).getId());
	}

}
