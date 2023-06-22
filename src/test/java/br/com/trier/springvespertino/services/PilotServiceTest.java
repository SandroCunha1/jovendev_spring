
package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/time.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto.sql")
class PilotServiceTest extends BaseTests {

	@Autowired
	PilotService pilotService;
	
	Country country;
	Team team;
	
	@BeforeEach
	void init() {
		country = new Country(1, "Brasil");
		team = new Team(1, "Ferrari");
	}

	@Test
	@DisplayName("Buscar por ID válido")
	void searchIdValid() {
		Pilot pilot = pilotService.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
	}

	@Test
	@DisplayName("Buscar por ID inválido")
	void searchIdInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> pilotService.findById(10));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Buscar todos")
	void searchAll() {
		assertEquals(3, pilotService.listAll().size());
	}

	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	void searchAllWithNoPilot() {
		var ex = assertThrows(ObjectNotFound.class, () -> pilotService.listAll());
		assertEquals("Nenhum piloto cadastrado", ex.getMessage());
	}

	@Test
	@DisplayName("Insert novo piloto")
	void insert() {
		Pilot pilot = new Pilot(null, "Jane Smith", country, team);
		pilotService.insert(pilot);
		assertEquals(1, pilotService.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals("Jane Smith", pilot.getName());
	}


	@Test
	@DisplayName("Update piloto")
	@Sql({ "classpath:/resources/sqls/piloto.sql" })
	void update() {
		Pilot pilot = pilotService.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
		pilot = new Pilot(1, "Updated Pilot", country, team);
		pilotService.update(pilot);
		assertEquals(3, pilotService.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals("Updated Pilot", pilot.getName());
	}

	@Test
	@DisplayName("Update piloto não existente")
	@Sql({ "classpath:/resources/sqls/piloto.sql" })
	void updateInvalid() {
		Pilot pilot = new Pilot(10, "Invalid Pilot",country, team );
		var ex = assertThrows(ObjectNotFound.class, () -> pilotService.update(pilot));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto")
	@Sql({ "classpath:/resources/sqls/piloto.sql" })
	void delete() {
		Pilot pilot = pilotService.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals("John Doe", pilot.getName());
		pilotService.delete(1);
		var ex = assertThrows(ObjectNotFound.class, () -> pilotService.findById(1));
		assertEquals("O piloto 1 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto não existente")
	void deleteInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> pilotService.delete(1));
		assertEquals("O piloto 10 não existe", ex.getMessage());
	}
}
