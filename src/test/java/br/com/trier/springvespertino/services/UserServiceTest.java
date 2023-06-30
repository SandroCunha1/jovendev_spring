package br.com.trier.springvespertino.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import br.com.trier.springvespertino.BaseTests;
import br.com.trier.springvespertino.models.User;
import br.com.trier.springvespertino.services.exceptions.IntegrityViolation;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;
import jakarta.transaction.Transactional;

@Transactional
class UserServiceTest extends BaseTests {

	@Autowired
	UserService userService;
	
	@Test
	@DisplayName("Buscar por ID válido")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchIdValid() {
		User usuario = userService.findById(2);
		assertNotNull(usuario);
		assertEquals(2, usuario.getId());
		assertEquals("Sandro1", usuario.getName());
		assertEquals("sandro1@gmail.com", usuario.getEmail());
		assertEquals("123", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Buscar por ID inválido")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchIdInvalid() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		userService.delete(10));
		assertEquals("O usuário 10 não existe", ex.getMessage());
	}
	
	@Test
	@DisplayName("Buscar todos")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchAll() {	
		assertEquals(3, userService.listAll().size());
	}
	
	@Test
	@DisplayName("Buscar todos com nenhum cadastro")
	void searchAllWithNoUser() {	
		var ex = assertThrows(ObjectNotFound.class, () ->
		userService.listAll());
		assertEquals("Nenhum usuário cadastrado", ex.getMessage());
	}
	
	@Test
	@DisplayName("Insert novo usuario")
	void insert() {	
		User usuario = new User(null, "Sandro", "sandrocunha@gmail.com", "1234", "ADMIN");
		userService.insert(usuario);
		assertEquals(1, userService.listAll().size());
		assertEquals(1, usuario.getId());
		assertEquals("Sandro", usuario.getName());
		assertEquals("sandrocunha@gmail.com", usuario.getEmail());
		assertEquals("1234", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Insert novo usuario com email duplicado")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void insertWithSameEmail() {	
		User usuario2 = new User(null, "João", "sandro1@gmail.com", "1234", "roles");
		var ex = assertThrows(IntegrityViolation.class, () ->
		userService.insert(usuario2));
		assertEquals("Email já existente: sandro1@gmail.com", ex.getMessage());

	}
	
	@Test
	@DisplayName("Update usuario")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void update() {	
		User usuario = userService.findById(2);
		assertNotNull(usuario);
		assertEquals(2, usuario.getId());
		assertEquals("Sandro1", usuario.getName());
		assertEquals("sandro1@gmail.com", usuario.getEmail());
		assertEquals("123", usuario.getPassword());	
		usuario = new User(2, "Sandro", "sandrocunha@gmail.com", "1234", "ADMIN");
		userService.update(usuario);
		assertEquals(3, userService.listAll().size());
		assertEquals(2, usuario.getId());
		assertEquals("Sandro", usuario.getName());
		assertEquals("sandrocunha@gmail.com", usuario.getEmail());
		assertEquals("1234", usuario.getPassword());	
	}
	
	@Test
	@DisplayName("Update usuario não existente")
	void updateInvalid() {
		User usuario = new User(1, "Sandro", "sandrocunha@gmail.com", "1234", "roles");
		var ex = assertThrows(ObjectNotFound.class, () ->
		userService.update(usuario));
		assertEquals("O usuário 1 não existe", ex.getMessage());
		
	}
	
	@Test
	@DisplayName("Update usuario com email cadastrado")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void updateInvalidSameEmail() {
		User usuario = new User(2, "Sandro", "sandro2@gmail.com", "1234", "roles");
		var ex = assertThrows(IntegrityViolation.class, () ->
		userService.update(usuario));
		assertEquals("Email já existente: sandro2@gmail.com", ex.getMessage());
		
	}
	
	@Test
	@DisplayName("Delete usuário")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void deleteUser() {	
		assertEquals(3, userService.listAll().size());
		userService.delete(2);
		assertEquals(2, userService.listAll().size());
		assertEquals(3, userService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Delete usuário que não existe")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void deleteUserNoExist() {	
		assertEquals(3, userService.listAll().size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		userService.delete(10));
		assertEquals("O usuário 10 não existe", ex.getMessage());
		assertEquals(3, userService.listAll().size());
		assertEquals(2, userService.listAll().get(0).getId());
	}
	
	@Test
	@DisplayName("Procura por nome")
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	void searchByName() {	
		assertEquals(3, userService.findByNameStartsWithIgnoreCase("s").size());
		assertEquals(3, userService.findByNameStartsWithIgnoreCase("Sandro").size());
		assertEquals(1, userService.findByNameStartsWithIgnoreCase("Sandro1").size());
		var ex = assertThrows(ObjectNotFound.class, () ->
		userService.findByNameStartsWithIgnoreCase("A").size());
		assertEquals("Nenhum usário inicia com A", ex.getMessage());
	}
	}
	
	
	
	
	
	
	


