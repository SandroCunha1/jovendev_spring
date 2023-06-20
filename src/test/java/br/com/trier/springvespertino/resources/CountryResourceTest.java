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
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.resources.exceptions.StandardError;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CountryResourceTest {

    @Autowired
    protected TestRestTemplate rest;

    @Test
    @DisplayName("Buscar país por ID")
    public void testFindCountryById() {
        ResponseEntity<Country> response = rest.getForEntity("/countrys/1", Country.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Country country = response.getBody();
        assertNotNull(country);
        assertEquals("Brasil", country.getName());
    }

    @Test
    @DisplayName("Buscar país por ID inexistente")
    public void testFindCountryByInvalidId() {
        ResponseEntity<Country> response = rest.getForEntity("/countrys/100", Country.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Cadastrar país")
    @Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
    public void testInsertCountry() {
        Country country = new Country(null, "Argentina");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Country> requestEntity = new HttpEntity<Country>(country, headers);
		ResponseEntity<Country> responseEntity = rest.exchange("/countrys", HttpMethod.POST, requestEntity, Country.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Country createdCountry = responseEntity.getBody();
        assertNotNull(createdCountry);
        assertEquals("Argentina", createdCountry.getName());
    }
    
    @Test
    @DisplayName("Criar país com mesmo nome")
    public void testCreateCountryWithSameName() {
        Country country = new Country(null, "Brasil");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Country> requestEntity = new HttpEntity<>(country, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/countrys", HttpMethod.POST, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Atualizar país")
    public void testUpdateCountry() {
        Country country = new Country(null, "Uruguai");
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Country> requestEntity = new HttpEntity<Country>(country, headers);
        ResponseEntity<Country> response = rest.exchange("/countrys/1", HttpMethod.PUT, requestEntity, Country.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Country updatedCountry = response.getBody();
        assertNotNull(updatedCountry);
        assertEquals("Uruguai", updatedCountry.getName());
    }
    
    @Test
    @DisplayName("Atualizar país com mesmo nome")
    public void testUpdateCountryWithSameName() {
        Country country = new Country(null, "Brasil");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Country> requestEntity = new HttpEntity<>(country, headers);
        ResponseEntity<Void> responseEntity = rest.exchange("/countrys/2", HttpMethod.PUT, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Excluir país")
    public void testDeleteCountry() {
        ResponseEntity<Void> response = rest.exchange("/countrys/1", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Excluir país inexistente")
    public void testDeleteCountryNonExist() {
        ResponseEntity<Void> responseEntity = rest.exchange("/countrys/100", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    @DisplayName("Listar todos os países")
    public void testListAllCountries() {
        ResponseEntity<List<Country>> response = rest.exchange("/countrys", HttpMethod.GET, null, new ParameterizedTypeReference<List<Country>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Country> countries = response.getBody();
        assertNotNull(countries);
        assertEquals(4, countries.size());
    }
    
    @Test
	@DisplayName("Listar todos os países sem países cadastrados")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
	public void testListAllTeamsInvalid() {
		ResponseEntity<StandardError> response = rest.getForEntity("/countrys", StandardError.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());		
	}
    
    @Test
    @DisplayName("Listar todos os países em ordem alfabética")
    public void testListAllCountriesOrderedByName() {
        ResponseEntity<List<Country>> response = rest.exchange("/countrys/name", HttpMethod.GET, null, new ParameterizedTypeReference<List<Country>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Country> countries = response.getBody();
        assertNotNull(countries);
        assertEquals(4, countries.size());
    }


    @Test
    @DisplayName("Buscar países por nome iniciando com")
    public void testFindCountriesByNameStartsWithIgnoreCase() {
        ResponseEntity<List<Country>> response = rest.exchange("/countrys/name/Br", HttpMethod.GET, null, new ParameterizedTypeReference<List<Country>>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Country> countries = response.getBody();
        assertNotNull(countries);
        assertEquals(1, countries.size());
    }
}

