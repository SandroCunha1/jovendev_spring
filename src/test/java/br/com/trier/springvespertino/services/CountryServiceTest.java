package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Country;
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
		assertNull(countryService.findById(5));
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchAll() {	
		assertEquals(4, countryService.listAll().size());
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
		countryService.delete(10);
		assertEquals(4, countryService.listAll().size());
		assertEquals(1, countryService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por nome")
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void searchByName() {	
		assertEquals(1, countryService.findByNameStartsWithIgnoreCase("br").size());
		assertEquals(2, countryService.findByNameStartsWithIgnoreCase("i").size());
		assertEquals(0, countryService.findByNameStartsWithIgnoreCase("x").size());
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
