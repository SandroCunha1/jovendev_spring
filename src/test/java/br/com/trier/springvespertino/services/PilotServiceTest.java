
package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/time.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto.sql")
class PilotServiceTest extends BaseTests {

	@Autowired
	PilotService service;
	
	@Autowired
	CountryService countryService;
	
	@Autowired
	TeamService teamService;
	

	@Test
	@DisplayName("Buscar por ID válido")
	void searchIdValid() {
		Pilot pilot = service.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
	}

	@Test
	@DisplayName("Buscar por ID inválido")
	void searchIdInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(10));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Buscar todos")
	void searchAll() {
		assertEquals(3, service.listAll().size());
	}

	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	void searchAllWithNoPilot() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.listAll());
		assertEquals("Nenhum piloto cadastrado", ex.getMessage());
	}

	@Test
	@DisplayName("Insert novo piloto")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	@Sql({"classpath:/resources/sqls/time.sql"})
	void insert() {
		Pilot pilot = new Pilot(null, "Jane Smith", countryService.findById(1), teamService.findById(1));
		service.insert(pilot);
		assertEquals(1, service.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals("Jane Smith", pilot.getName());
	}


	@Test
	@DisplayName("Update piloto")
	void update() {
		Pilot pilot = service.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
		pilot = new Pilot(1, "Updated Pilot", countryService.findById(1), teamService.findById(1));
		service.update(pilot);
		assertEquals(3, service.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals("Updated Pilot", pilot.getName());
	}

	@Test
	@DisplayName("Update piloto não existente")
	void updateInvalid() {
		Pilot pilot = new Pilot(10, "Invalid Pilot",countryService.findById(1), teamService.findById(1) );
		var ex = assertThrows(ObjectNotFound.class, () -> service.update(pilot));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto")
	void delete() {
		Pilot pilot = service.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
		service.delete(1);
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(1));
		assertEquals("O piloto 1 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto não existente")
	void deleteInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.delete(10));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Encontra pilotos por nome")
	void findByNameStartsWithIgnoreCase() {
		List<Pilot> lista = service.findByNameStartsWithIgnoreCase("J");
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pilotos por nome sem nomes iguais")
	void findByNameStartsWithIgnoreCaseInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByNameStartsWithIgnoreCase("y"));
		assertEquals("Nenhum piloto cadastrada com nome: y", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra pilotos por país")
	void findByCountryOrderByName() {
		List<Pilot> lista = service.findByCountryOrderByName(countryService.findById(1));
		assertEquals(1, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pilotos por país sem nenhum com este país")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void findByCountryOrderByNameInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByCountryOrderByName(countryService.findById(1)));
		assertEquals("Nenhum piloto cadastrada no país : Brasil", ex.getMessage());
	}


	@Test
	@DisplayName("Encontra pilotos por time")
	void findByTeamOrderByName() {
		List<Pilot> lista = service.findByTeamOrderByName(teamService.findById(1));
		assertEquals(1, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pilotos por time sem times encontrados")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/time.sql"})
	void findByTeamOrderByNameInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByTeamOrderByName(teamService.findById(1)));
		assertEquals("Nenhum piloto cadastrado na equipe : Ferrari", ex.getMessage());
	}

}
