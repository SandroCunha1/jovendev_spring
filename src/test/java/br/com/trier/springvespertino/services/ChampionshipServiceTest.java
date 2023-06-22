package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
class ChampionshipServiceTest extends BaseTests{



	@Autowired
	ChampionshipService campeonatoService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchIdValid() {
		Championship campeonato = campeonatoService.findById(1);
		assertNotNull(campeonato);
		assertEquals(1, campeonato.getId());
		assertEquals(1990, campeonato.getYear());	
		assertEquals("Formula 1", campeonato.getDescription());
	}
	
	@Test
	@DisplayName("Buscar por ID inválido")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchIdInvalid() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.findById(10));
		assertEquals("O campeonato 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchAll() {	
		assertEquals(3, campeonatoService.listAll().size());
	}
	
	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	void searchAllWithNoUser() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.listAll());
		assertEquals("Nenhum campeonato cadastrado", ex.getMessage());
	}
	
	@Test
	@DisplayName("Insert novo campeonato")
	void insert() {	
		Championship campeonato = new Championship(null, "insert", 1999);
		campeonatoService.insert(campeonato);
		assertEquals(1, campeonatoService.listAll().size());
		assertEquals(1, campeonato.getId());
		assertEquals("insert", campeonato.getDescription());
		assertEquals(1999, campeonato.getYear());
	}
	
	@Test
	@DisplayName("Insert novo campeonato ano invalido <1990 || >anoAtual+1")
	void insertIvalid() {	
		Championship campeonato = new Championship(null, "insert", 1980);
		var ex = assertThrows(IntegrityViolation.class, () ->
		campeonatoService.insert(campeonato));
		assertEquals("O campeonato deve estar ente 1990 e %s".formatted(LocalDate.now().plusYears(1).getYear()), ex.getMessage());
	}
	
	@Test
	@DisplayName("Insert novo campeonato ano nulo")
	void insertNull() {	
		Championship campeonato = new Championship(null, "insert", null);
		var ex = assertThrows(IntegrityViolation.class, () ->
		campeonatoService.insert(campeonato));
		assertEquals("O ano não pode ser nulo!", ex.getMessage());
	}
	
	@Test
	@DisplayName("Update campeonato")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void update() {	
		Championship campeonato = campeonatoService.findById(1);
		assertNotNull(campeonato);
		assertEquals(1, campeonato.getId());
		assertEquals(1990, campeonato.getYear());	
		assertEquals("Formula 1", campeonato.getDescription());	
		campeonato = new Championship(1, "update", 1999);
		campeonatoService.update(campeonato);
		assertEquals(3, campeonatoService.listAll().size());
		assertEquals(1, campeonato.getId());
		assertEquals("update", campeonato.getDescription());
		assertEquals(1999, campeonato.getYear());	
	}
	
	@Test
	@DisplayName("Update campeonato não existente")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void updateInvalid() {	 	
		Championship campeonato = new Championship(10, "update", 1990);
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.update(campeonato));
		assertEquals("O campeonato 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Delete usuário")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void deleteChampionship() {	
		assertEquals(3, campeonatoService.listAll().size());
		campeonatoService.delete(1);
		assertEquals(2, campeonatoService.listAll().size());
		assertEquals(2, campeonatoService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Delete usuário que não existe")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void deleteChampionshipNoExist() {	
		assertEquals(3, campeonatoService.listAll().size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.delete(10));
		assertEquals("O campeonato 10 não existe", ex.getMessage());
		assertEquals(3, campeonatoService.listAll().size());
		assertEquals(1, campeonatoService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por ano")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchByYear() {	
		assertEquals(1, campeonatoService.findByYear(1990).size());
		assertEquals(1, campeonatoService.findByYear(1991).size());

	}
	
	@Test
	@DisplayName("Procura por ano não existente")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchByYearNonExist() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.findByYear(1997));
		assertEquals("Nenhum campeonato em 1997", ex.getMessage());

	}
	
	@Test
	@DisplayName("Procura por ano inválido")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchByYearInvalid() {	
		var ex = assertThrows(IntegrityViolation.class, () ->
		campeonatoService.findByYear(1980));
		assertEquals("O campeonato deve estar ente 1990 e %s".formatted(LocalDate.now().plusYears(1).getYear()), ex.getMessage());

	}
	
	@Test
	@DisplayName("Procura por descrição")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void findByDescription() {	
		assertEquals(2, campeonatoService.findByDescriptionContaining("1").size());
		assertEquals(3, campeonatoService.findByDescriptionContaining("f").size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.findByDescriptionContaining("y"));
		assertEquals("Nenhum campeonato contem y", ex.getMessage());
	}

	
	@Test
	@DisplayName("Procura por ano entre")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void findByYearBetween() {	
		assertEquals(2, campeonatoService.findByYearBetween(1990,1991).size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		campeonatoService.findByYearBetween(2000,2010));
		assertEquals("Nenhum campeonato entre 2000 e 2010", ex.getMessage());
	}
	
	
	@Test
	@DisplayName("Procura por ano entre integrity violation ano<1990 ou ano>ano atual + 1")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void findByYearBetweenViolation() {	
		var ex = assertThrows(IntegrityViolation.class, () ->
		campeonatoService.findByYearBetween(1980,1990));
		assertEquals("O campeonato deve estar ente 1990 e %s".formatted(LocalDate.now().plusYears(1).getYear()), ex.getMessage());
		
		ex = assertThrows(IntegrityViolation.class, () ->
		campeonatoService.findByYearBetween(1990,2990));
		assertEquals("O campeonato deve estar ente 1990 e %s".formatted(LocalDate.now().plusYears(1).getYear()), ex.getMessage());

	}
	
	
}
