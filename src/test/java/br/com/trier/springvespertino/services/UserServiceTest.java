package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.User;
import jakarta.transaction.Transactional;

@Transactional
class UserServiceTest extends BaseTests {

	@Autowired
	UserService userService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchIdValid() {
		User usuario = userService.findById(1);
		assertNotNull(usuario);
		assertEquals(1, usuario.getId());
		assertEquals("Sandro1", usuario.getName());
		assertEquals("sandro1@gmail.com", usuario.getEmail());
		assertEquals("123", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Buscar por ID inválido")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchIdInvalid() {	
		assertNull(userService.findById(4));
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchAll() {	
		assertEquals(3, userService.listAll().size());
	}
	
	@Test
	@DisplayName("Insert novo usuario")
	void insert() {	
		User usuario = new User(null, "Sandro", "sandrocunha@gmail.com", "1234");
		userService.insert(usuario);
		assertEquals(1, userService.listAll().size());
		assertEquals(1, usuario.getId());
		assertEquals("Sandro", usuario.getName());
		assertEquals("sandrocunha@gmail.com", usuario.getEmail());
		assertEquals("1234", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Update usuario")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void update() {	
		User usuario = userService.findById(1);
		assertNotNull(usuario);
		assertEquals(1, usuario.getId());
		assertEquals("Sandro1", usuario.getName());
		assertEquals("sandro1@gmail.com", usuario.getEmail());
		assertEquals("123", usuario.getPassword());	
		usuario = new User(1, "Sandro", "sandrocunha@gmail.com", "1234");
		userService.update(usuario);
		assertEquals(3, userService.listAll().size());
		assertEquals(1, usuario.getId());
		assertEquals("Sandro", usuario.getName());
		assertEquals("sandrocunha@gmail.com", usuario.getEmail());
		assertEquals("1234", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Update usuario não existente")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void updateInvalid() {	 	
	}
	
	@Test
	@DisplayName("Delete usuário")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void deleteUser() {	
		assertEquals(3, userService.listAll().size());
		userService.delete(1);
		assertEquals(2, userService.listAll().size());
		assertEquals(2, userService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Delete usuário que não existe")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void deleteUserNoExist() {	
		assertEquals(3, userService.listAll().size());
		userService.delete(10);
		assertEquals(3, userService.listAll().size());
		assertEquals(1, userService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por nome")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchByName() {	
		assertEquals(3, userService.findByNameStartsWithIgnoreCase("s").size());
		assertEquals(3, userService.findByNameStartsWithIgnoreCase("Sandro").size());
		assertEquals(1, userService.findByNameStartsWithIgnoreCase("Sandro1").size());
		assertEquals(0, userService.findByNameStartsWithIgnoreCase("A").size());
	}
	
	
	
	
	
	
	

}
