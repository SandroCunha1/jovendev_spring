
package br.com.trier.springvespertino.services;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Runway;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pista.sql")
class RunwayServiceTest extends BaseTests {

	@Autowired
	RunwayService runwayService;
	
	@Autowired
	CountryService countryService;
	


	@Test
	@DisplayName("Buscar por ID válido")
	void searchIdValid() {
		Runway runway = runwayService.findById(1);
		assertNotNull(runway);
		assertEquals(1, runway.getId());
		assertEquals("Interlagos", runway.getName());
	}

	@Test
	@DisplayName("Buscar por ID inválido")
	void searchIdInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.findById(10));
		assertEquals("A pista 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Buscar todos")
	void searchAll() {
		assertEquals(3, runwayService.listAll().size());
	}

	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	void searchAllWithNoRunway() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.listAll());
		assertEquals("Nenhuma pista cadastrado", ex.getMessage());
	}

	@Test
	@DisplayName("Insert novo pista")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void insert() {
		Runway runway = new Runway(null, 1000,  "Internacional", countryService.findById(1));
		runwayService.insert(runway);
		assertEquals(1, runwayService.listAll().size());
		assertEquals(1, runway.getId());
		assertEquals("Internacional", runway.getName());
	}
	
	@Test
	@DisplayName("Insert novo pista com tamanho invalido")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void insertInvalid() {
		Runway runway = new Runway(null, 0,  "Internacional", countryService.findById(1));
		var ex = assertThrows(IntegrityViolation.class, () -> runwayService.insert(runway));
		assertEquals("Tamanho da pista inválido", ex.getMessage());

	}


	@Test
	@DisplayName("Update pista")
	void update() {
		Runway runway = new Runway(1, 1000,  "Internacional", countryService.findById(1));
		runwayService.update(runway);
		assertEquals(3, runwayService.listAll().size());
		assertEquals(1, runway.getId());
		assertEquals("Internacional", runway.getName());
	}

	@Test
	@DisplayName("Update pista não existente")
	void updateInvalid() {
		Runway runway = new Runway(10, 1000,  "Internacional", countryService.findById(1));
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.update(runway));
		assertEquals("A pista 10 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete pista")
	void delete() {
		Runway runway = runwayService.findById(1);
		assertNotNull(runway);
		assertEquals(1, runway.getId());
		assertEquals("Interlagos", runway.getName());
		runwayService.delete(1);
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.findById(1));
		assertEquals("A pista 1 não existe", ex.getMessage());
	}

	@Test
	@DisplayName("Delete pista não existente")
	void deleteInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.delete(10));
		assertEquals("A pista 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Encontra pistas por nome")
	void findByNameStartsWithIgnoreCase() {
		List<Runway> lista = runwayService.findByNameStartsWithIgnoreCase("I");
		assertEquals(1, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pistas por nome sem nomes iguais")
	void findByNameStartsWithIgnoreCaseInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.findByNameStartsWithIgnoreCase("y"));
		assertEquals("Nenhuma pista cadastrada com nome: y", ex.getMessage());
	}

	@Test
	@DisplayName("Encontra pistas por país")
	void findByCountryOrderByName() {
		List<Runway> lista = runwayService.findByCountryOrderBySizeDesc(countryService.findById(1));
		assertEquals(1, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pistas por país sem nenhum com este país")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/pais.sql"})
	void findByCountryOrderByNameInvalid() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.findByCountryOrderBySizeDesc(countryService.findById(1)));
		assertEquals("Nenhuma pista cadastrada com o país: Brasil", ex.getMessage());
	}
	
	@Test
	@DisplayName("Encontra pistas por tamanho entre")
	void findBySizeBetween() {
		List<Runway> lista = runwayService.findBySizeBetween(1000,2000);
		assertEquals(2, lista.size());
	}
	
	@Test
	@DisplayName("Encontra pistas por tamanho entre sem nenhuma pista com este tamanho")
	void findBySizeBetweenNotFound() {
		var ex = assertThrows(ObjectNotFound.class, () -> runwayService.findBySizeBetween(5000,6000));
		assertEquals("Nenhuma pista cadastrada com tamanho entre 5000 e 6000", ex.getMessage());
	}
	



}
