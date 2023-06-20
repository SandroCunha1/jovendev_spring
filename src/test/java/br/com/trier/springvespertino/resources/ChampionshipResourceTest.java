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
import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.resources.exceptions.StandardError;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/campeonato.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChampionshipResourceTest {

    @Autowired
    protected TestRestTemplate rest;

    @Test
    @DisplayName("Buscar campeonato por ID")
    public void testFindChampionshipById() {
        ResponseEntity<Championship> response = rest.getForEntity("/championships/1", Championship.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Championship championship = response.getBody();
        assertNotNull(championship);
        assertEquals("Formula 1", championship.getDescription());
    }

    @Test
    @DisplayName("Buscar campeonato por ID inexistente")
    public void testFindChampionshipByInvalidId() {
        ResponseEntity<Championship> response = rest.getForEntity("/championships/100", Championship.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Cadastrar campeonato")
    @Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
    public void testInsertChampionship() {
        Championship championship = new Championship(null, "Campeonato Argentino", 2023);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Championship> requestEntity = new HttpEntity<Championship>(championship, headers);
		ResponseEntity<Championship> responseEntity = rest.exchange("/championships", HttpMethod.POST, requestEntity, Championship.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Championship createdChampionship = responseEntity.getBody();
        assertNotNull(createdChampionship);
        assertEquals("Campeonato Argentino", createdChampionship.getDescription());
    }
    
    @Test
    @DisplayName("Cadastrar campeonato com ano inválido")
    public void testInsertChampionshipWithInvalidYear() {
        Championship championship = new Championship(null, "Campeonato Argentino", 1980);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Championship> requestEntity = new HttpEntity<>(championship, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/championships", HttpMethod.POST, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    

    @Test
    @DisplayName("Atualizar campeonato")
    public void testUpdateChampionship() {
        Championship championship = new Championship(null, "Campeonato Uruguaio", 2023);
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Championship> requestEntity = new HttpEntity<Championship>(championship, headers);
        ResponseEntity<Championship> response = rest.exchange("/championships/1", HttpMethod.PUT, requestEntity, Championship.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Championship updatedChampionship = response.getBody();
        assertNotNull(updatedChampionship);
        assertEquals("Campeonato Uruguaio", updatedChampionship.getDescription());
    }
    
    @Test
    @DisplayName("Atualizar campeonato com ano inválido")
    public void testUpdateChampionshipWithInvalidYear() {
        Championship championship = new Championship(null, "Campeonato Uruguaio", 1980);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Championship> requestEntity = new HttpEntity<>(championship, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/championships/1", HttpMethod.PUT, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Excluir campeonato")
    public void testDeleteChampionship() {
        ResponseEntity<Void> response = rest.exchange("/championships/1", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    @DisplayName("Excluir campeonato inexistente")
    public void testDeleteChampionshipNonExist() {
        ResponseEntity<Void> responseEntity = rest.exchange("/championships/100", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Listar todos os campeonatos")
    public void testListAllChampionships() {
        ResponseEntity<List<Championship>> response = rest.exchange("/championships", HttpMethod.GET, null, new ParameterizedTypeReference<List<Championship>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(3, championships.size());
    }
    
    @Test
	@DisplayName("Listar todos os campeonatos sem campeonatos cadastrados")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
	public void testListAllTeamsInvalid() {
		ResponseEntity<StandardError> response = rest.getForEntity("/championships", StandardError.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());		
	}

    @Test
    @DisplayName("Buscar campeonatos por descrição contendo")
    public void testFindChampionshipsByDescriptionContaining() {
        ResponseEntity<List<Championship>> response = rest.exchange("/championships/description/1", HttpMethod.GET, null, new ParameterizedTypeReference<List<Championship>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(2, championships.size());
    }

    @Test
    @DisplayName("Buscar campeonatos por ano")
    public void testFindChampionshipsByYear() {
        ResponseEntity<List<Championship>> response = rest.exchange("/championships/year/1990", HttpMethod.GET, null, new ParameterizedTypeReference<List<Championship>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(1, championships.size());
    }
    
    @Test
    @DisplayName("Buscar campeonatos por ano inválido")
    public void testFindChampionshipsByInvalidYear() {
        ResponseEntity<StandardError> response = rest.exchange("/championships/year/1970", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Buscar campeonatos por ano entre dois valores")
    public void testFindChampionshipsByYearBetween() {
        ResponseEntity<List<Championship>> response = rest.exchange("/championships/year/1990/1991", HttpMethod.GET, null, new ParameterizedTypeReference<List<Championship>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Championship> championships = response.getBody();
        assertNotNull(championships);
        assertEquals(2, championships.size());
    }
    
    @Test
    @DisplayName("Buscar campeonatos por intervalo de ano inválido")
    public void testFindChampionshipsByInvalidYearRange() {
        ResponseEntity<StandardError> response = rest.exchange("/championships/year/1980/2024", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

