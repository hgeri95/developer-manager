package hu.homework.developermanager.controller;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.DeveloperFilterRequest;
import hu.homework.developermanager.dto.DeveloperWithProjectsDTO;
import hu.homework.developermanager.model.Technology;
import hu.homework.developermanager.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/developer")
@RequiredArgsConstructor
public class DeveloperRestController {

	private final DeveloperService developerService;

	@PostMapping
	public DeveloperWithProjectsDTO createDeveloper(@RequestBody @Valid DeveloperRequest developerCreationRequest) {
		return developerService.createDeveloper(developerCreationRequest);
	}

	@PostMapping("/filter")
	public List<DeveloperWithProjectsDTO> filterDevelopers(@RequestBody @Valid DeveloperFilterRequest filterRequest) {
		return developerService.filterDevelopers(filterRequest);
	}

	@GetMapping
	public List<DeveloperWithProjectsDTO> getAllDevelopers() {
		return developerService.getAllDevelopers();
	}

	@GetMapping("/{id}")
	public DeveloperWithProjectsDTO getDeveloperById(@PathVariable Long id) {
		return developerService.getDeveloperById(id);
	}

	@PutMapping("/{id}")
	public DeveloperWithProjectsDTO updateDeveloper(@PathVariable Long id, @RequestBody @Valid DeveloperRequest developer) {
		return developerService.updateDeveloper(id, developer);
	}

	@DeleteMapping("/{id}")
	public void deleteDeveloper(@PathVariable Long id) {
		developerService.deleteDeveloper(id);
	}

	@GetMapping("/technology")
	public List<String> getAllAvailableTechnology() {
		log.info("Get all available technology");
		return Arrays.stream(Technology.values()).map(Enum::name).collect(Collectors.toList());
	}
}
