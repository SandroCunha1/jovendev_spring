package br.com.trier.springvespertino.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import br.com.trier.springvespertino.models.Championship;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/campeonato.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/usuario.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChampionshipResourceTest {

    @Autowired
    protected TestRestTemplate rest;


	
	@SuppressWarnings("unused")
	private ResponseEntity<Championship> getUser(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Championship.class);
	}

	private ResponseEntity<List<Championship>> getUsers(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Championship>>() {
		});
	}
	
	@Test
	@DisplayName("Buscar por id")
	public void testGetOk() {

		HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
	    ResponseEntity<Championship> response = rest.exchange("/championships/1", HttpMethod.GET, new HttpEntity<>(headers), Championship.class);
	    assertEquals(response.getStatusCode(), HttpStatus.OK);

	    Championship user = response.getBody();
	    assertEquals("Formula 1", user.getDescription());
	}

	@Test
	@DisplayName("Buscar por id inexistente")
	public void testGetNotFound() {
			HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
		    ResponseEntity<Championship> response = rest.exchange("/championships/100", HttpMethod.GET, new HttpEntity<>(headers), Championship.class);
		    assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Cadastrar Campeonato")
	public void testCreateChamp() {
			Championship championship = new Championship(1, "Campeonato Uruguaio", 1990);
		 	HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    
		    HttpEntity<Championship> requestEntity = new HttpEntity<Championship>(championship, headers);
		    ResponseEntity<Championship> responseEntity = rest.exchange(
		            "/championships",
		            HttpMethod.POST,
		            requestEntity,
		            Championship.class
		    		);
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		    Championship user = responseEntity.getBody();
		    assertEquals("Campeonato Uruguaio", user.getDescription());
	}
	
	@Test
	@DisplayName("Atualizar camp")
	public void testUpdateCamp() {
			Championship championship = new Championship(1, "nome atualizado", 1990);
		    HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    HttpEntity<Championship> requestEntity = new HttpEntity<Championship>(championship, headers);
		    ResponseEntity<Championship> responseEntity = rest.exchange(
		    		"/championships/1",
		    		HttpMethod.PUT,
		    		requestEntity,
		    		Championship.class
		    		);
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		    Championship user = responseEntity.getBody();
		    assertEquals("nome atualizado", user.getDescription());
	}
	
	@Test
	@DisplayName("Excluir campeonato")
	public void testDeleteChamp() {
		    ResponseEntity<Void> responseEntity = rest.exchange("/championships/1", HttpMethod.DELETE, new HttpEntity<>(getHeader("sandro1@gmail.com", "123")), Void.class);
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	@DisplayName("Excluir campeonato que não existe")
	public void testDeleteChampNonExist() {
	    ResponseEntity<Void> responseEntity = rest.exchange("/championships/100", HttpMethod.DELETE, new HttpEntity<>(getHeader("sandro1@gmail.com", "123")), Void.class);
	    assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Listar todos os usuários")
	public void testListAllUsers() {
		    ResponseEntity<List<Championship>> responseEntity = getUsers("/championships", getHeader("sandro1@gmail.com", "123"));
		    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		    List<Championship> users = responseEntity.getBody();
		    assertEquals(3, users.size());
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
	

    @Test
    @DisplayName("Buscar campeonatos por descrição contendo")
    public void testFindChampionshipsByDescriptionContaining() {
        ResponseEntity<List<Championship>> response = getUsers("/championships/description/1", getHeader("sandro1@gmail.com", "123"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(2, championships.size());
    }

    @Test
    @DisplayName("Buscar campeonatos por ano")
    public void testFindChampionshipsByYear() {  
        ResponseEntity<List<Championship>> response = getUsers("/championships/year/1990", getHeader("sandro1@gmail.com", "123"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(1, championships.size());
    }
    

    @Test
    @DisplayName("Buscar campeonatos por ano entre dois valores")
    public void testFindChampionshipsByYearBetween() {
        ResponseEntity<List<Championship>> response = getUsers("/championships/year/1990/1991", getHeader("sandro1@gmail.com", "123"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(2, championships.size());
    }

}

