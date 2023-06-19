package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
class TeamServiceTest extends BaseTests{


	@Autowired
	TeamService timeService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void searchIdValid() {
		Team time = timeService.findById(1);
		assertNotNull(time);
		assertEquals(1, time.getId());
		assertEquals("Ferrari", time.getName());	
	}
	
	@Test
	@DisplayName("Buscar por ID inválido")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void searchIdInvalid() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		timeService.findById(10));
		assertEquals("A equipe 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void searchAll() {	
		assertEquals(3, timeService.listAll().size());
	}
	
	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	void searchAllWithNoTeam() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		timeService.listAll());
		assertEquals("Nenhuma equipe cadastrada", ex.getMessage());
	}
	
	@Test
	@DisplayName("Insert novo time")
	void insert() {	
		Team time = new Team(null, "insert");
		timeService.insert(time);
		assertEquals(1, timeService.listAll().size());
		assertEquals(1, time.getId());
		assertEquals("insert", time.getName());	
	}
	
	@Test
	@DisplayName("Insert novo time com nome duplicado")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void insertWithSameEmail() {	
		Team time = new Team(null, "Ferrari");
		var ex = assertThrows(IntegrityViolation.class, () ->
		timeService.insert(time));
		assertEquals("Nome já cadastrado : Ferrari", ex.getMessage());

	}
	
	
	@Test
	@DisplayName("Update time")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void update() {	
		Team time = timeService.findById(1);
		assertNotNull(time);
		assertEquals(1, time.getId());
		assertEquals("Ferrari", time.getName());	
		time = new Team(1, "update");
		timeService.update(time);
		assertEquals(3, timeService.listAll().size());
		assertEquals(1, time.getId());
		assertEquals("update", time.getName());	
	}
	
	@Test
	@DisplayName("Update time não existente")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void updateInvalid() {	 	
		Team time = new Team(10, "ABC");
		var ex = assertThrows(ObjectNotFound.class, () ->
		timeService.update(time));
		assertEquals("A equipe 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Delete usuário")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void deleteTeam() {	
		assertEquals(3, timeService.listAll().size());
		timeService.delete(1);
		assertEquals(2, timeService.listAll().size());
		assertEquals(3, timeService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Delete usuário que não existe")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void deleteTeamNoExist() {	
		assertEquals(3, timeService.listAll().size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		timeService.delete(10));
		assertEquals("A equipe 10 não existe", ex.getMessage());		
		assertEquals(3, timeService.listAll().size());
		assertEquals(1, timeService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por nome")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void searchByName() {	
		assertEquals(1, timeService.findByNameStartsWithIgnoreCase("f").size());
		assertEquals(2, timeService.findByNameStartsWithIgnoreCase("m").size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		timeService.findByNameStartsWithIgnoreCase("A"));
		assertEquals("Nenhuma equipe inicia com A", ex.getMessage());
	}
	
	@Test
	@DisplayName("Ordena por nome")
	@Sql({"classpath:/resources/sqls/time.sql"})
	void searchAllAndOrderByName() {	
		assertEquals(1, timeService.findAllByOrderByName().get(0).getId());
		assertEquals(3, timeService.findAllByOrderByName().get(1).getId());
		assertEquals(2, timeService.findAllByOrderByName().get(2).getId());
	}
}
