package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Championship;
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
		assertNull(campeonatoService.findById(4));
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void searchAll() {	
		assertEquals(3, campeonatoService.listAll().size());
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
		campeonatoService.delete(10);
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
	@DisplayName("Procura por descrição")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void findByDescription() {	
		assertEquals(2, campeonatoService.findByDescriptionContaining("1").size());
		assertEquals(3, campeonatoService.findByDescriptionContaining("f").size());
		assertEquals(0, campeonatoService.findByDescriptionContaining("y").size());
	}

	
	@Test
	@DisplayName("Procura por ano entre")
	@Sql({"classpath:/resources/sqls/campeonato.sql"})
	void findByYearBetween() {	
		assertEquals(2, campeonatoService.findByYearBetween(1990,1991).size());
		assertEquals(1, campeonatoService.findByYearBetween(1980,1990).size());
		assertEquals(0, campeonatoService.findByYearBetween(2000,2010).size());
	}
}
