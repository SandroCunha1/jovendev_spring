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
import br.com.trier.springvespertino.config.jwt.LoginDTO;
import br.com.trier.springvespertino.models.dto.UserDTO;
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/usuario.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserResourceTest {

	@Autowired
	protected TestRestTemplate rest;
	

	
	@SuppressWarnings("unused")
	private ResponseEntity<UserDTO> getUser(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), UserDTO.class);
	}

	private ResponseEntity<List<UserDTO>> getUsers(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<UserDTO>>() {
		});
	}
	
	@Test
	@DisplayName("Buscar por id")
	public void testGetOk() {

		HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
	    ResponseEntity<UserDTO> response = rest.exchange("/users/2", HttpMethod.GET, new HttpEntity<>(headers), UserDTO.class);
	    assertEquals(response.getStatusCode(), HttpStatus.OK);

	    UserDTO user = response.getBody();
	    assertEquals("Sandro1", user.getName());
	}

	@Test
	@DisplayName("Buscar por id inexistente")
	public void testGetNotFound() {
			HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
		    ResponseEntity<UserDTO> response = rest.exchange("/users/100", HttpMethod.GET, new HttpEntity<>(headers), UserDTO.class);
		    assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Cadastrar usuário")
	public void testCreateUser() {
		 UserDTO dto = new UserDTO(null, "nome", "email", "senha", "ADMIN");
		 	HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
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

		    UserDTO dto = new UserDTO(null, "nome atualizado", "email atualizado", "senha atualizada", "roles");
		    HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    HttpEntity<UserDTO> requestEntity = new HttpEntity<UserDTO>(dto, headers);
		    ResponseEntity<UserDTO> responseEntity = rest.exchange("/users/2", HttpMethod.PUT, requestEntity,
		            UserDTO.class);
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		    UserDTO user = responseEntity.getBody();
		    assertEquals("nome atualizado", user.getName());
	}
	
	@Test
	@DisplayName("Excluir usuário")
	public void testDeleteUser() {
		    ResponseEntity<Void> responseEntity = rest.exchange("/users/2", HttpMethod.DELETE, new HttpEntity<>(getHeader("sandro1@gmail.com", "123")), Void.class);
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	@DisplayName("Excluir usuário que não existe")
	public void testDeleteUserNonExist() {
	    ResponseEntity<Void> responseEntity = rest.exchange("/users/100", HttpMethod.DELETE, new HttpEntity<>(getHeader("sandro1@gmail.com", "123")), Void.class);
	    assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Listar todos os usuários")
	public void testListAllUsers() {
		    ResponseEntity<List<UserDTO>> responseEntity = getUsers("/users", getHeader("sandro1@gmail.com", "123"));
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		    List<UserDTO> users = responseEntity.getBody();
		    assertEquals(3, users.size());
	}
	
	
	@Test
	@DisplayName("Buscar usuários por nome iniciando com")
	public void testFindUsersByNameStartsWithIgnoreCase() {
	    ResponseEntity<List<UserDTO>> responseEntity = getUsers("/users/name/Sandro", getHeader("sandro1@gmail.com", "123"));
	    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	    List<UserDTO> users = responseEntity.getBody();
	    assertEquals(3, users.size());
	}
	
	@Test
	@DisplayName("Obter Token")
	@Sql({"classpath:/resources/sqls/limpa_tabelas.sql"})
	@Sql({"classpath:/resources/sqls/usuario.sql"})
	public void getToken() {
		LoginDTO loginDTO = new LoginDTO("sandro1@gmail.com", "123");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginDTO> requestEntity = new HttpEntity<>(loginDTO, headers);
		ResponseEntity<String> responseEntity = rest.exchange(
				"/auth/token", 
				HttpMethod.POST,  
				requestEntity,    
				String.class   
				);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		String token = responseEntity.getBody();
		System.out.println("****************"+token);
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		ResponseEntity<List<UserDTO>> response =  rest.exchange("/users", HttpMethod.GET, new HttpEntity<>(null, headers),new ParameterizedTypeReference<List<UserDTO>>() {});
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}
	
	private HttpHeaders  getHeader(String email, String senha) {
		LoginDTO loginDTO = new LoginDTO(email, senha);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<LoginDTO> requestEntity = new HttpEntity<>(loginDTO, headers);
	    ResponseEntity<String> responseEntity = rest.exchange(
	            "/auth/token",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	    );
	    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	    HttpHeaders headersR = new HttpHeaders();
	    headersR.setBearerAuth(responseEntity.getBody());
	    return headersR;
	}

}
