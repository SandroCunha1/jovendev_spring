package br.com.trier.springvespertino.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.resources.exceptions.StandardError;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/time.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeamResourceTest {

	@Autowired
	protected TestRestTemplate rest;

	UtilToken token = new UtilToken();
	private ResponseEntity<Team> getTeam(String url, HttpHeaders headers) {
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Team.class);
    }

	private ResponseEntity<List<Team>> getTeams(String url, HttpHeaders headers) {
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
    }

	@Test
	@DisplayName("Buscar por id")
	public void testGetOk() {
		
		HttpHeaders headers =  token.getHeader("sandro1@gmail.com", "123");
        ResponseEntity<Team> response = getTeam("/teams/1", headers);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        Team team = response.getBody();
        assertEquals("Ferrari", team.getName());
	}

	@Test
	@DisplayName("Buscar por id inexistente")
	public void testGetNotFound() {
		ResponseEntity<Team> response = getTeam("/teams/100");
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	@DisplayName("Cadastrar time")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
	public void testCreateTeam() {
		Team team = new Team(null, "Time Teste");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Team> requestEntity = new HttpEntity<Team>(team, headers);
		ResponseEntity<Team> responseEntity = rest.exchange("/teams", HttpMethod.POST, requestEntity, Team.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		Team createdTeam = responseEntity.getBody();
		assertEquals("Time Teste", createdTeam.getName());
	}
	
	@Test
    @DisplayName("Criar time com mesmo nome")
    public void testCreateTeamWithSameName() {
        Team team = new Team(null, "Ferrari");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Team> requestEntity = new HttpEntity<>(team, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/teams", HttpMethod.POST, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

	@Test
	@DisplayName("Atualizar time")
	public void testUpdateTeam() {
		Team team = new Team(null, "Time Atualizado");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Team> requestEntity = new HttpEntity<Team>(team, headers);
		ResponseEntity<Team> responseEntity = rest.exchange("/teams/1", HttpMethod.PUT, requestEntity, Team.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		Team updatedTeam = responseEntity.getBody();
		assertEquals("Time Atualizado", updatedTeam.getName());
	}

	@Test
    @DisplayName("Atualizar time com mesmo nome")
    public void testUpdateTeamWithSameName() {
        Team team = new Team(null, "Ferrari");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Team> requestEntity = new HttpEntity<>(team, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/teams/2", HttpMethod.PUT, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

	@Test
	@DisplayName("Excluir time")
	public void testDeleteTeam() {
		ResponseEntity<Void> responseEntity = rest.exchange("/teams/1", HttpMethod.DELETE, null, Void.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}
	
	 @Test
	    @DisplayName("Excluir time inexistente")
	    public void testDeleteTeamNonExist() {
	        ResponseEntity<Void> responseEntity = rest.exchange("/teams/100", HttpMethod.DELETE, null, Void.class);
	        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	    }

	@Test
	@DisplayName("Listar todos os times")
	public void testListAllTeams() {
		ResponseEntity<List<Team>> responseEntity = getTeams("/teams");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<Team> teams = responseEntity.getBody();
		assertEquals(3, teams.size());
	}
	
	@Test
	@DisplayName("Listar todos os times sem times cadastrados")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
	public void testListAllTeamsInvalid() {
		ResponseEntity<StandardError> response = rest.getForEntity("/teams", StandardError.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());		
	}
	
	@Test
	@DisplayName("Listar todos os times ordenados por nome")
	public void testListAllTeamsOrderByName() {
		ResponseEntity<List<Team>> responseEntity = getTeams("/teams/name");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<Team> teams = responseEntity.getBody();
		assertEquals(3, teams.size());
	}

	@Test
	@DisplayName("Buscar times por nome iniciando com")
	public void testFindTeamsByNameStartsWithIgnoreCase() {
		ResponseEntity<List<Team>> responseEntity = getTeams("/teams/name/Ferrari");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<Team> teams = responseEntity.getBody();
		assertEquals(1, teams.size());
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
