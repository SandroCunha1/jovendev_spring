package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.PilotRace;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/time.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pista.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/campeonato.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/corrida.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto-corrida.sql")
class PilotRaceServiceTest extends BaseTests {


	@Autowired
	PilotService pilotService;
	
	@Autowired
	RaceService raceService;
	
	@Autowired
	PilotRaceService service;
	

	@Test
	@DisplayName("Buscar por ID válido")
	void searchIdValid() {
		PilotRace pilotR = service.findById(1);
		assertNotNull(pilotR);
		assertEquals(1, pilotR.getId());
		assertEquals(1, pilotR.getPlacement());
		assertEquals(1, pilotR.getPilot().getId());
		assertEquals(1, pilotR.getRace().getId());
	}

	@Test
	@DisplayName("Buscar por ID inválido")
	void searchIdInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(10));
		assertEquals("O relacionamento piloto/corrida 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Buscar todos")
	void searchAll() {
		assertEquals(3, service.listAll().size());
	}

	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	void searchAllWithNoPilotRace() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.listAll());
		assertEquals("Nenhum piloto/corrida cadastrado", ex.getMessage());
	}

	@Test
	@DisplayName("Insert novo piloto/corrida")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	@Sql({"classpath:/resources/sqls/time.sql"})
	@Sql({"classpath:/resources/sqls/piloto.sql"})
	@Sql({"classpath:/resources/sqls/pista.sql"})
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	@Sql({"classpath:/resources/sqls/corrida.sql"})
	void insert() {
		PilotRace pilot = new PilotRace(null, 1, pilotService.findById(1), raceService.findById(1));
		service.insert(pilot);
		assertEquals(1, service.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals(1, pilot.getPlacement());
	}


	@Test
	@DisplayName("Update piloto/corrida")
	void update() {
		PilotRace pilot = service.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals(1, service.findById(1).getPlacement());
		pilot = new PilotRace(1, 2, pilotService.findById(1), raceService.findById(1));
		service.update(pilot);
		assertEquals(3, service.listAll().size());
		assertEquals(1, pilot.getId());
		assertEquals(2, service.findById(1).getPlacement());
	}

	@Test
	@DisplayName("Update piloto/corrida não existente")
	void updateInvalid() {
		PilotRace pilot = new PilotRace(10, 1, pilotService.findById(1), raceService.findById(1));
		var ex = assertThrows(ObjectNotFound.class, () -> service.update(pilot));
		assertEquals("O relacionamento piloto/corrida 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto/corrida")
	void delete() {
		PilotRace pilot = service.findById(1);
		assertNotNull(pilot);
		assertEquals(1, pilot.getId());
		assertEquals(1, pilot.getPlacement());
		service.delete(1);
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(1));
		assertEquals("O relacionamento piloto/corrida 1 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete piloto não existente")
	void deleteInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.delete(10));
		assertEquals("O relacionamento piloto/corrida 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Encontra piloto/corrida por colocação")
	void findByPlacement() {
		List<PilotRace> lista = service.findByPlacement(1);
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra piloto/corrida por colocação sem colocação igual")
	void findByPlacementNonExist() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByPlacement(3));
		assertEquals("Nenhum piloto cadastrado com colocação: 3", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra piloto/corrida por piloto")
	void findByPilot() {
		List<PilotRace> lista = service.findByPilotOrderByPlacement(pilotService.findById(1));
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra piloto/corrida por piloto sem nenhum com este piloto")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	@Sql({"classpath:/resources/sqls/time.sql"})
	@Sql({"classpath:/resources/sqls/piloto.sql"})
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	@Sql({"classpath:/resources/sqls/pista.sql"})
	@Sql({"classpath:/resources/sqls/corrida.sql"})
	void findByPilotNonExist() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByPilotOrderByPlacement(pilotService.findById(1)));
		assertEquals("Piloto não cadastrado : %s".formatted(pilotService.findById(1).getName()), ex.getMessage());
	}


	@Test
	@DisplayName("Encontra piloto/corrida por corrida")
	void findByRace() {
		List<PilotRace> lista = service.findByRaceOrderByPlacement(raceService.findById(1));
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra piloto/corrida por corrida sem corrida encontrados")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	@Sql({"classpath:/resources/sqls/time.sql"})
	@Sql({"classpath:/resources/sqls/piloto.sql"})
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	@Sql({"classpath:/resources/sqls/pista.sql"})
	@Sql({"classpath:/resources/sqls/corrida.sql"})
	void findByRaceNonExist() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByRaceOrderByPlacement(raceService.findById(1)));
		assertEquals("Nenhum piloto associado a corrida de id: 1", ex.getMessage());
	}

}
