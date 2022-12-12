package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.DeveloperFilterRequest;
import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.DeveloperWithProjectsDTO;
import hu.homework.developermanager.mapper.DeveloperMapper;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.entity.Developer;
import hu.homework.developermanager.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeveloperService {

	private final DeveloperRepository developerRepository;
	private final DeveloperMapper developerMapper;

	public DeveloperWithProjectsDTO createDeveloper(DeveloperRequest developerCreationRequest) {
		log.info("Create developer {}", developerCreationRequest);
		Developer savedDeveloper = developerRepository.save(createDeveloperFromRequest(developerCreationRequest));
		log.debug("Developer saved {}", savedDeveloper);
		return developerMapper.entityToDTO(savedDeveloper);
	}

	public List<DeveloperWithProjectsDTO> getAllDevelopers() {
		log.info("Get all developers");
		List<Developer> developers = developerRepository.findAll();
		log.debug("{} developers found", developers.size());
		return developers.stream().map(developerMapper::entityToDTO).collect(Collectors.toList());
	}

	public DeveloperWithProjectsDTO getDeveloperById(Long id) {
		log.info("Get developer by id {}", id);
		Developer developer = developerRepository.findById(id).orElse(null);
		if (developer != null) {
			log.debug("Developer found {} by id {}", developer, id);
			return developerMapper.entityToDTO(developer);
		} else {
			log.info("Cannot find developer by id {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer with id not exists!");
		}
	}

	public DeveloperWithProjectsDTO updateDeveloper(Long id, DeveloperRequest developerRequest) {
		log.info("Update developer {} with data {}", id, developerRequest);
		Optional<Developer> developer = developerRepository.findById(id);
		if (developer.isEmpty()) {
			log.info("Developer with id {} not exists", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer with id not exists!");
		} else {
			Developer updatedDeveloper = developerRepository.save(updateDeveloperAttributes(developer.get(), developerRequest));
			log.debug("Developer updated {}", updatedDeveloper);
			return developerMapper.entityToDTO(updatedDeveloper);
		}
	}

	@Transactional
	public void deleteDeveloper(Long id) {
		log.info("Delete developer {}", id);
		Developer developer = developerRepository.findById(id).orElse(null);
		if (developer != null) {
			developer.getProjects().forEach(project -> {
				project.removeDeveloper(developer.getId());
			});
			developerRepository.delete(developer);
		} else {
			log.info("Developer with id {} not exists", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer not exists!");
		}
	}

	public List<DeveloperWithProjectsDTO> filterDevelopers(DeveloperFilterRequest filterRequest) {
		log.info("Filter developers by {}", filterRequest);

		if (!isFilterRequestDatesExistInPair(filterRequest)) {
			log.info("Invalid filter request {}", filterRequest);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing date filtering criteria!");
		}

		List<Developer> developers = developerRepository.findAll();
		final List<Developer> filteredDevelopers;
		if (filterRequest.projectStart() != null) {
			validateIfStartDateBeforeFinishDate(filterRequest.projectStart(), filterRequest.projectFinish());
			filteredDevelopers = filterDevelopersByProjectPeriodAndSkills(developers, filterRequest);
		} else {
			filteredDevelopers = filterDevelopersBySkills(developers, filterRequest.requiredSkills());
		}

		log.debug("Filtered developers {}", filteredDevelopers);
		return filteredDevelopers.stream().map(developerMapper::entityToDTO).collect(Collectors.toList());
	}

	private static List<Developer> filterDevelopersByProjectPeriodAndSkills(List<Developer> developers, DeveloperFilterRequest filterRequest) {
		return developers
				.stream()
				.filter(developer ->
						developer.isAvailableBetween(filterRequest.projectStart(), filterRequest.projectFinish())
								&& developer.hasRequiredSkills(Objects.requireNonNullElse(filterRequest.requiredSkills(), Collections.emptySet()))).collect(Collectors.toList());
	}

	private static List<Developer> filterDevelopersBySkills(List<Developer> developers, Set<Skill> requiredSkills) {
		return developers
				.stream()
				.filter(developer -> developer.hasRequiredSkills(Objects.requireNonNullElse(requiredSkills, Collections.emptySet()))).collect(Collectors.toList());
	}

	private static boolean isFilterRequestDatesExistInPair(DeveloperFilterRequest filterRequest) {
		return !(filterRequest.projectStart() != null && filterRequest.projectFinish() == null || filterRequest.projectStart() == null && filterRequest.projectFinish() != null);
	}

	private static void validateIfStartDateBeforeFinishDate(LocalDate start, LocalDate finish) {
		if (!start.isBefore(finish)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is not before finish date!");
		}
	}

	private static Developer updateDeveloperAttributes(Developer developer, DeveloperRequest developerRequest) {
		developer.setName(developerRequest.name());
		developer.setAge(developerRequest.age());
		developer.setSkills(developerRequest.skills());

		return developer;
	}

	private static Developer createDeveloperFromRequest(DeveloperRequest developerRequest) {
		return Developer.builder()
				.name(developerRequest.name())
				.age(developerRequest.age())
				.skills(developerRequest.skills())
				.projects(new HashSet<>())
				.build();
	}
}
