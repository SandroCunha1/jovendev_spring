
package br.com.trier.springvespertino.services;
import static org.junit.jupiter.api.Assertions.*;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import br.com.trier.springvespertino.utils.DateUtils;
import jakarta.transaction.Transactional;

@Transactional
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pista.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/campeonato.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/corrida.sql")
class RaceServiceTest extends BaseTests {

	@Autowired
	RaceService service;
	
	@Autowired
	RunwayService runwayService;
	
	@Autowired
	ChampionshipService championshipService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	void searchIdValid() {
		Race race = service.findById(1);
		assertNotNull(race);
		assertEquals(1, race.getId());
		assertEquals(1990, race.getDate().getYear());
	}

	@Test
	@DisplayName("Buscar por ID inválido")
	void searchIdInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(10));
		assertEquals("A corrida 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Buscar todos")
	void searchAll() {
		assertEquals(3, service.listAll().size());
	}

	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	void searchAllWithNoRace() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.listAll());
		assertEquals("Nenhuma corrida cadastrada", ex.getMessage());
	}

	@Test
	@DisplayName("Insert novo corrida")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	@Sql({"classpath:/resources/sqls/pista.sql"})
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void insert() {
		Race race = new Race(null, DateUtils.dateBrToZoneDate("01/10/1990"), runwayService.findById(2), championshipService.findById(1) );
		service.insert(race);
		assertEquals(1, service.listAll().size());
		assertEquals(1, race.getId());
		assertEquals(1990, race.getDate().getYear());
	}


	@Test
	@DisplayName("Update corrida")
	void update() {
		Race race = new Race(1, DateUtils.dateBrToZoneDate("01/10/1990"), runwayService.findById(2), championshipService.findById(1) );
		service.update(race);
		assertEquals(3, service.listAll().size());
		assertEquals(1, race.getId());
		assertEquals(1990, race.getDate().getYear());
	}
	
	@Test
	@DisplayName("Update corrida data não condiz com ano do campeonato")
	void updateInvalidDate() {
		Race race = new Race(1, DateUtils.dateBrToZoneDate("01/10/2000"), runwayService.findById(2), championshipService.findById(1) );
		var ex = assertThrows(IntegrityViolation.class, () -> service.update(race));
		assertEquals("Ano da corrida: 2000 Deve ser o mesmo ano do campeonato: 1990 ", ex.getMessage());
	}

	@Test
	@DisplayName("Update corrida não existente")
	void updateInvalid() {
		Race race = new Race(10, ZonedDateTime.now(), runwayService.findById(2), championshipService.findById(1) );
		var ex = assertThrows(ObjectNotFound.class, () -> service.update(race));
		assertEquals("A corrida 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete corrida")
	void delete() {
		Race race = service.findById(1);
		assertNotNull(race);
		assertEquals(1, race.getId());
		assertEquals(1990, race.getDate().getYear());
		service.delete(1);
		var ex = assertThrows(ObjectNotFound.class, () -> service.findById(1));
		assertEquals("A corrida 1 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete corrida não existente")
	void deleteInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.delete(10));
		assertEquals("A corrida 10 não existe", ex.getMessage());
	}

	
	@Test
	@DisplayName("Encontra por data")
	void findByDate() {
		List<Race> lista = service.findByDate("21/06/1990");
		assertEquals(1, lista.size());			
	}
	
	@Test
	@DisplayName("Encontra por data não existe")
	void findByDateNonExist() {
		List<Race> lista = service.findByDate("21/06/1990");
		assertEquals(1, lista.size());	
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByDate("21/06/2000"));
		assertEquals("Nenhuma corrida cadastrada com data 21/06/2000", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra por data entre")
	void findByDateBetween() {
		List<Race> lista = service.findByDateBetween("01/10/1990", "01/10/1992");
		assertEquals(2, lista.size());	
	}
	
	@Test
	@DisplayName("Encontra por data entre não existe")
	void findByDateBetweenNonExist() {	
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByDateBetween("01/10/2000", "01/10/2001"));
		assertEquals("Nenhuma corrida cadastrada com data entre 01/10/2000 e 01/10/2001", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra por campeonato ordenado por data")
	void findByChampionshipOrderByDate() {
		List<Race> lista = service.findByChampionshipOrderByDate(championshipService.findById(1));
		assertEquals(1, lista.size());
	}
	
	@Test
	@DisplayName("Encontra por campeonato ordenado por data não existe")
	void findByChampionshipOrderByDateNonExist() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByChampionshipOrderByDate(championshipService.findById(3)));
		assertEquals("Nenhuma corrida cadastrada no campeonato Formula 1", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra por pista ordenado por data")
	void findByRunwayOrderByDate() {
		List<Race> lista = service.findByRunwayOrderByDate(runwayService.findById(1));
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra por pista ordenado por data não existe")
	void findByRunwayOrderByDateNonExist() {
		var ex = assertThrows(ObjectNotFound.class, () -> service.findByRunwayOrderByDate(runwayService.findById(3)));
		assertEquals("Nenhuma corrida cadastrada na pista Maricas", ex.getMessage());
	}
	


}
