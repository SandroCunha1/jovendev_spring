package br.com.trier.springvespertino.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import br.com.trier.springvespertino.models.dto.PilotsByRunwayDTO;
import br.com.trier.springvespertino.models.dto.PilotsTeamAndChampionshipDTO;
import br.com.trier.springvespertino.models.dto.RaceCountryYearDTO;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/limpa_tabelas.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/campeonato.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/time.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pista.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/corrida.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/piloto-corrida.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/usuario.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportResourceTest {

	@Autowired
    protected TestRestTemplate rest;
	
	private ResponseEntity<RaceCountryYearDTO> getUsersRaceCountry(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<RaceCountryYearDTO>() {
		});
	}
	
	private ResponseEntity<PilotsTeamAndChampionshipDTO> getUsersTeamAndChampionship(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PilotsTeamAndChampionshipDTO>() {
		});
	}
	
	private ResponseEntity<PilotsByRunwayDTO> getUsersRunway(String url, HttpHeaders headers) {
		return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PilotsByRunwayDTO>() {
		});
	}
	
	 @Test
	    @DisplayName("Buscar corridas por país e ano")
	    public void testFindByCountryAndYear() {
		 	HttpHeaders h = getHeader("sandro1@gmail.com", "123");
	        ResponseEntity<RaceCountryYearDTO> response = getUsersRaceCountry("/report/reces-by-country-year/1/1990", h);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        RaceCountryYearDTO championships = response.getBody();
	        assertNotNull(championships);
	    }
	 
	 @Test
	    @DisplayName("Buscar piloto por time e campeonato")
	    public void getUsersTeamAndChampionship() {
		 	HttpHeaders h = getHeader("sandro1@gmail.com", "123");
	        ResponseEntity<PilotsTeamAndChampionshipDTO> response = getUsersTeamAndChampionship("/report/pilots-by-team-championship/1/1", h);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        PilotsTeamAndChampionshipDTO championships = response.getBody();
	        assertNotNull(championships);
	    }
	 
	 @Test
	    @DisplayName("Buscar piloto por corrida")
	    public void getUsersRunway() {
		 	HttpHeaders h = getHeader("sandro1@gmail.com", "123");
	        ResponseEntity<PilotsByRunwayDTO> response = getUsersRunway("/report/pilots-by-runway/1", h);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        PilotsByRunwayDTO championships = response.getBody();
	        assertNotNull(championships);
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
