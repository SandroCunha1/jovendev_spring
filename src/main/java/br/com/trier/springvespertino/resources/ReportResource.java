package br.com.trier.springvespertino.resources;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.trier.springvespertino.models.Championship;
import br.com.trier.springvespertino.models.Country;
import br.com.trier.springvespertino.models.Pilot;
import br.com.trier.springvespertino.models.PilotRace;
import br.com.trier.springvespertino.models.Race;
import br.com.trier.springvespertino.models.Team;
import br.com.trier.springvespertino.models.dto.PilotDTO;
import br.com.trier.springvespertino.models.dto.PilotsByRunwayDTO;
import br.com.trier.springvespertino.models.dto.PilotsTeamAndChampionshipDTO;
import br.com.trier.springvespertino.models.dto.RaceCountryYearDTO;
import br.com.trier.springvespertino.models.dto.RaceDTO;
import br.com.trier.springvespertino.services.ChampionshipService;
import br.com.trier.springvespertino.services.CountryService;
import br.com.trier.springvespertino.services.PilotRaceService;
import br.com.trier.springvespertino.services.PilotService;
import br.com.trier.springvespertino.services.RaceService;
import br.com.trier.springvespertino.services.RunwayService;
import br.com.trier.springvespertino.services.TeamService;
import br.com.trier.springvespertino.services.exceptions.ObjectNotFound;

@RestController
@RequestMapping("/report")
public class ReportResource {

	@Autowired
	private CountryService countryService;

	@Autowired
	private ChampionshipService championshipService;

	@Autowired
	private RunwayService runwayService;

	@Autowired
	private RaceService raceService;

	@Autowired
	private PilotRaceService pilotRaceService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private PilotService pilotService;

	@GetMapping("/reces-by-country-year/{countryId}/{year}")
	public ResponseEntity<RaceCountryYearDTO> findRaceByCountryAndYear(@PathVariable Integer countryId,
			@PathVariable Integer year) {

		Country country = countryService.findById(countryId);

		List<RaceDTO> raceDTOs = runwayService.findByCountryOrderBySizeDesc(country).stream()
		.flatMap(speedway -> {
			try {
				return raceService.findByRunwayOrderByDate(speedway).stream();
			} catch (ObjectNotFound e) {
				return Stream.empty();
			}
		})
		.filter(race -> race.getDate().getYear() == year)
		.map(Race::toDTO)
		.toList();

		return ResponseEntity.ok(new RaceCountryYearDTO(year, country.getName(), raceDTOs.size(), raceDTOs));
	}

	@GetMapping("/pilots-by-runway/{runwayId}")
	public ResponseEntity<PilotsByRunwayDTO> findPilotByRunway(@PathVariable Integer runwayId) {	
		List<PilotDTO> pilots = raceService.findByRunwayOrderByDate(runwayService.findById(runwayId)).stream()
				.flatMap(race -> {
					try {
						return pilotRaceService.findByRaceOrderByPlacement(race).stream();
					} catch (ObjectNotFound e) {
						return Stream.empty();
					}
				})
				.map(PilotRace::toDTO)
				.map(pilotRace -> pilotService.findById(pilotRace.getPilotId()))
				.map(Pilot::toDTO)
				.collect(Collectors.toMap(PilotDTO::getId, Function.identity(), (p1, p2) -> p1))
				.values()
				.stream()
				.toList();



		return ResponseEntity.ok(new PilotsByRunwayDTO(runwayService.findById(runwayId).getName(), pilots.size(), pilots));		

	}

	@GetMapping("/pilots-by-team-championship/{teamId}/{championshipId}")
	public ResponseEntity<PilotsTeamAndChampionshipDTO> findPilotByTeamAndChampionship(@PathVariable Integer teamId,@PathVariable Integer championshipId) {

		Championship championship = championshipService.findById(championshipId);
		Team team = teamService.findById(teamId);

		List<PilotDTO> pilots = raceService.findByChampionshipOrderByDate(championship).stream()
			.flatMap(race -> {
				try {
					return pilotRaceService.findByRaceOrderByPlacement(race).stream();
				} catch (ObjectNotFound e) {
					return Stream.empty();
				}
			})
			.map(PilotRace::toDTO)
			.map(pilotRace -> pilotService.findById(pilotRace.getPilotId()))
			.filter(pilot -> pilot.getTeam().equals(team))
			.map(Pilot::toDTO)
			.collect(Collectors.toMap(PilotDTO::getId, Function.identity(), (p1, p2) -> p1))
			.values()
			.stream()
			.toList();

		return ResponseEntity.ok(new PilotsTeamAndChampionshipDTO(team.getName(), championship.getDescription(), championship.getYear(), pilots));	
	}


}