package br.com.trier.springvespertino;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import br.com.trier.springvespertino.services.ChampionshipService;
import br.com.trier.springvespertino.services.CountryService;
import br.com.trier.springvespertino.services.PilotRaceService;
import br.com.trier.springvespertino.services.PilotService;
import br.com.trier.springvespertino.services.RaceService;
import br.com.trier.springvespertino.services.RunwayService;
import br.com.trier.springvespertino.services.TeamService;
import br.com.trier.springvespertino.services.UserService;
import br.com.trier.springvespertino.services.impl.ChampionshipServiceImpl;
import br.com.trier.springvespertino.services.impl.CountryServiceImpl;
import br.com.trier.springvespertino.services.impl.PilotRaceServiceImpl;
import br.com.trier.springvespertino.services.impl.PilotServiceImpl;
import br.com.trier.springvespertino.services.impl.RaceServiceImpl;
import br.com.trier.springvespertino.services.impl.RunwayServiceImpl;
import br.com.trier.springvespertino.services.impl.TeamServiceImpl;
import br.com.trier.springvespertino.services.impl.UserServiceImpl;

@TestConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class BaseTests {

	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}
	
	@Bean
	public CountryService countryService() {
		return new CountryServiceImpl();
	}
	
	@Bean
	public TeamService teamService() {
		return new TeamServiceImpl();
	}
	
	@Bean
	public ChampionshipService championshipService() {
		return new ChampionshipServiceImpl();
	}
	
	@Bean
	public PilotService pilotService() {
		return new PilotServiceImpl();
	}
	
	@Bean
	public PilotRaceService pilotRaceService() {
		return new PilotRaceServiceImpl();
	}
	
	@Bean
	public RaceService raceService() {
		return new RaceServiceImpl();
	}
	
	@Bean
	public RunwayService runwayService() {
		return new RunwayServiceImpl();
	}

}
