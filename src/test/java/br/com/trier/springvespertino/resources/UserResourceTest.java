package br.com.trier.springvespertino.resources;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import br.com.trier.springvespertino.SpringVespertinoApplication;
import br.com.trier.springvespertino.models.dto.UserDTO;
import br.com.trier.springvespertino.resources.exceptions.StandardError;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/usuario.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserResourceTest {

	@Autowired
	protected TestRestTemplate rest;
	
	
	private ResponseEntity<UserDTO> getUser(String url) {
		return rest.getForEntity(url, UserDTO.class);
	}

	private ResponseEntity<List<UserDTO>> getUsers(String url) {
		return rest.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDTO>>() {
		});
	}
	
	@Test
	@DisplayName("Buscar por id")
	public void testGetOk() {
		ResponseEntity<UserDTO> response = getUser("/users/1");
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		UserDTO user = response.getBody();
		assertEquals("Sandro1", user.getName());
	}

	@Test
	@DisplayName("Buscar por id inexistente")
	public void testGetNotFound() {
		ResponseEntity<UserDTO> response = getUser("/users/100");
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Cadastrar usuário")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	public void testCreateUser() {
		UserDTO dto = new UserDTO(null, "nome", "email", "senha");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserDTO> requestEntity = new HttpEntity<UserDTO>(dto, headers);
		ResponseEntity<UserDTO> responseEntity = rest.exchange(
	            "/users", 
	            HttpMethod.POST,  
	            requestEntity,    
	            UserDTO.class   
	    );
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		UserDTO user = responseEntity.getBody();
		assertEquals("nome", user.getName());
	}
	
	@Test
	@DisplayName("Atualizar usuário")
	public void testUpdateUser() {
		UserDTO dto = new UserDTO(null, "nome atualizado", "email atualizado", "senha atualizada");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserDTO> requestEntity = new HttpEntity<UserDTO>(dto, headers);
		ResponseEntity<UserDTO> responseEntity = rest.exchange("/users/1", HttpMethod.PUT, requestEntity,
				UserDTO.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		UserDTO user = responseEntity.getBody();
		assertEquals("nome atualizado", user.getName());
	}
	
	@Test
	@DisplayName("Excluir usuário")
	public void testDeleteUser() {
		 ResponseEntity<Void> responseEntity = rest.exchange("/users/1", HttpMethod.DELETE, null, Void.class);
		 assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	@DisplayName("Excluir usuário que não existe")
	public void testDeleteUserNonExist() {
		 ResponseEntity<Void> responseEntity = rest.exchange("/users/100", HttpMethod.DELETE, null, Void.class);
		 assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Listar todos os usuários")
	public void testListAllUsers() {
		ResponseEntity<List<UserDTO>> responseEntity = getUsers("/users");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<UserDTO> users = responseEntity.getBody();
		assertEquals(3, users.size());
	}
	
	@Test
	@DisplayName("Listar todos os usuários sem usuários cadastrados")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
	public void testListAllTeamsInvalid() {
		ResponseEntity<StandardError> response = rest.getForEntity("/users", StandardError.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());		
	}
	
	@Test
	@DisplayName("Buscar usuários por nome iniciando com")
	public void testFindUsersByNameStartsWithIgnoreCase() {
		ResponseEntity<List<UserDTO>> responseEntity = getUsers("/users/name/Sandro");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<UserDTO> users = responseEntity.getBody();
		assertEquals(3, users.size());
	}

}
