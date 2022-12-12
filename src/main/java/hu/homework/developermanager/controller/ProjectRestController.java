package hu.homework.developermanager.controller;

import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.dto.ProjectWithDevelopersDTO;
import hu.homework.developermanager.service.ProjectService;
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
import java.util.List;

@RestController
@RequestMapping("/project")
@Slf4j
@RequiredArgsConstructor
public class ProjectRestController {

	private final ProjectService projectService;

	@PostMapping
	public ProjectWithDevelopersDTO createProject(@RequestBody @Valid ProjectRequest projectRequest) {
		return projectService.createProject(projectRequest);
	}

	@GetMapping
	public List<ProjectWithDevelopersDTO> getAllProjects() {
		return projectService.getAllProjects();
	}

	@GetMapping("/{id}")
	public ProjectWithDevelopersDTO getProjectById(@PathVariable Long id) {
		return projectService.getProjectById(id);
	}

	@PutMapping("/{id}")
	public ProjectWithDevelopersDTO updateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
		return projectService.updateProject(id, projectRequest);
	}

	@DeleteMapping("/{id}")
	public void deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
	}
}
