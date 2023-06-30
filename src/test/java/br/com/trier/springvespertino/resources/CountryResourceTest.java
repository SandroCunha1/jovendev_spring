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
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.resources.exceptions.StandardError;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/pais.sql")
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/resources/sqls/usuario.sql")
@SpringBootTest(classes = SpringVespertinoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CountryResourceTest {

    @Autowired
    protected TestRestTemplate rest;

    
    private ResponseEntity<List<Country>> getCountrys(String url, HttpHeaders headers) {
    	return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
	}
    
    @Test
    @DisplayName("Buscar país por ID")
    public void testFindCountryById() {
    	ResponseEntity<Country> response = rest.exchange(
                "/countrys/1",
                HttpMethod.GET,
                new HttpEntity<>(getHeader("sandro1@gmail.com", "123")),
                Country.class
            );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Country country = response.getBody();
            assertNotNull(country);
            assertEquals("Brasil", country.getName());
    }

    @Test
    @DisplayName("Buscar país por ID inexistente")
    public void testFindCountryByInvalidId() {
    	ResponseEntity<Country> response = rest.exchange(
                "/countrys/100",
                HttpMethod.GET,
                new HttpEntity<>(getHeader("sandro1@gmail.com", "123")),
                Country.class
            );
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Cadastrar país")
    @Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
    @Sql({ "classpath:/resources/sqls/usuario.sql" })
    public void testInsertCountry() {
    	Country country = new Country(null, "Argentina");
        HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
        HttpEntity<Country> requestEntity = new HttpEntity<>(country, headers);
        ResponseEntity<Country> responseEntity = rest.exchange(
            "/countrys",
            HttpMethod.POST,
            requestEntity,
            Country.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Country createdCountry = responseEntity.getBody();
        assertNotNull(createdCountry);
        assertEquals("Argentina", createdCountry.getName());
    }
    
    @Test
    @DisplayName("Criar país com mesmo nome")
    public void testCreateCountryWithSameName() {
    	Country country = new Country(null, "Brasil");
        HttpEntity<Country> requestEntity = new HttpEntity<>(country, getHeader("sandro1@gmail.com", "123"));
        ResponseEntity<Void> responseEntity = rest.exchange("/countrys", HttpMethod.POST, requestEntity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
         
    }

    @Test
    @DisplayName("Atualizar país")
    public void testUpdateCountry() {
    	Country country = new Country(null, "País Atualizado");
		HttpEntity<Country> requestEntity = new HttpEntity<Country>(country, getHeader("sandro1@gmail.com", "123"));
		ResponseEntity<Country> responseEntity = rest.exchange("/countrys/1", HttpMethod.PUT, requestEntity, Country.class);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		Country updatedCountry = responseEntity.getBody();
		assertEquals("País Atualizado", updatedCountry.getName());
    }
    
    @Test
    @DisplayName("Atualizar país com mesmo nome")
    public void testUpdateCountryWithSameName() {     
        Country country = new Country(null, "Brasil");
		HttpEntity<Country> requestEntity = new HttpEntity<Country>(country, getHeader("sandro1@gmail.com", "123"));
		ResponseEntity<Country> responseEntity = rest.exchange("/countrys/2", HttpMethod.PUT, requestEntity, Country.class);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Excluir país")
    public void testDeleteCountry() {
    	HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
	    ResponseEntity<Void> responseEntity = rest.exchange("/countrys/1", HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
	    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("Excluir país inexistente")
    public void testDeleteCountryNonExist() {
    	ResponseEntity<Void> responseEntity = rest.exchange("/countrys/100", HttpMethod.DELETE, new HttpEntity<>(getHeader("sandro1@gmail.com", "123")), Void.class);
		 assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    @DisplayName("Listar todos os países")
    public void testListAllCountries() {
    	ResponseEntity<List<Country>> response = rest.exchange(
    	        "/countrys",
    	        HttpMethod.GET,
    	        new HttpEntity<>(getHeader("sandro1@gmail.com", "123")),
    	        new ParameterizedTypeReference<List<Country>>() {}
    	    );
    	    assertEquals(HttpStatus.OK, response.getStatusCode());
    	    List<Country> countries = response.getBody();
    	    assertNotNull(countries);
    	    assertEquals(4, countries.size());
    }
    
    @Test
    @DisplayName("Listar todos os países ordenado por nome")
    public void testListAllCountriesOrder() {
    	ResponseEntity<List<Country>> response = rest.exchange(
    	        "/countrys/name",
    	        HttpMethod.GET,
    	        new HttpEntity<>(getHeader("sandro1@gmail.com", "123")),
    	        new ParameterizedTypeReference<List<Country>>() {}
    	    );
    	    assertEquals(HttpStatus.OK, response.getStatusCode());
    	    List<Country> countries = response.getBody();
    	    assertNotNull(countries);
    	    assertEquals(4, countries.size());
    }
    
    @Test
	@DisplayName("Listar todos os países sem países cadastrados")
	@Sql({ "classpath:/resources/sqls/limpa_tabelas.sql" })
    @Sql({ "classpath:/resources/sqls/usuario.sql" })
	public void testListAllTeamsInvalid() {
    	HttpHeaders headers = getHeader("sandro1@gmail.com", "123");
	    ResponseEntity<StandardError> response = rest.exchange("/countrys", HttpMethod.GET, new HttpEntity<>(headers), StandardError.class);
	    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());	
	}
    


    @Test
    @DisplayName("Buscar países por nome iniciando com")
    public void testFindCountriesByNameStartsWithIgnoreCase() {
    	ResponseEntity<List<Country>> responseEntity = getCountrys("/countrys/name/Br", getHeader("sandro1@gmail.com", "123"));
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		List<Country> country = responseEntity.getBody();
		assertEquals(1, country.size());

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

