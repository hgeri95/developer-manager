package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.dto.ProjectWithDevelopersDTO;
import hu.homework.developermanager.mapper.ProjectMapper;
import hu.homework.developermanager.model.entity.Developer;
import hu.homework.developermanager.model.entity.Project;
import hu.homework.developermanager.repository.DeveloperRepository;
import hu.homework.developermanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final DeveloperRepository developerRepository;
	private final ProjectMapper projectMapper;

	@Transactional
	public ProjectWithDevelopersDTO createProject(ProjectRequest projectRequest) {
		log.info("Create project {}", projectRequest);
		validateProjectDates(projectRequest);
		Project project = addDevelopersToProject(createProjectFromRequest(projectRequest), projectRequest.developerIds());
		Project savedProject = projectRepository.save(project);

		log.debug("Project saved {}", savedProject);
		return projectMapper.entityToDTO(savedProject);
	}

	public List<ProjectWithDevelopersDTO> getAllProjects() {
		log.info("Get all projects");
		List<Project> projects = projectRepository.findAll();
		log.debug("{} projects found", projects.size());
		return projects.stream().map(projectMapper::entityToDTO).collect(Collectors.toList());
	}

	public ProjectWithDevelopersDTO getProjectById(Long id) {
		log.info("Get project by id {}", id);
		Project project = projectRepository.findById(id).orElse(null);
		if (project != null) {
			log.debug("Project {} found by id {}", project, id);
			return projectMapper.entityToDTO(project);
		} else {
			log.info("Project with id {} not found", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with id not exists!");
		}
	}

	@Transactional
	public ProjectWithDevelopersDTO updateProject(Long id, ProjectRequest projectRequest) {
		log.info("Update project {} with data {}", id, projectRequest);
		validateProjectDates(projectRequest);
		Optional<Project> project = projectRepository.findById(id);
		if (project.isEmpty()) {
			log.info("Project with id {} not exists", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with id not exists!");
		} else {
			Project updatedProject = projectRepository.save(updateProjectAttributes(project.get(), projectRequest));
			log.debug("Project updated {}", updatedProject);
			return projectMapper.entityToDTO(updatedProject);
		}
	}

	@Transactional
	public void deleteProject(Long id) {
		log.info("Delete project with id {}", id);
		Project project = projectRepository.findById(id).orElse(null);
		if (project != null) {
			project.getDevelopers().forEach(developer -> {
				project.removeDeveloper(developer.getId());
			});
			projectRepository.delete(project);
		} else {
			log.info("Project with id {} not exists", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with id not exists!");
		}
	}

	private static void validateProjectDates(ProjectRequest projectRequest) {
		if (projectRequest.finish().isBefore(projectRequest.start())) {
			log.info("Invalid project dates start {} and finish {}", projectRequest.start(), projectRequest.finish());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after finish date!");
		}
	}

	private Project updateProjectAttributes(Project project, ProjectRequest projectRequest) {
		project.setName(projectRequest.name());
		project.setStart(projectRequest.start());
		project.setFinish(projectRequest.finish());
		project = updateDevelopersInProject(project, projectRequest.developerIds());
		return project;
	}

	private Project updateDevelopersInProject(Project project, Set<Long> developerIds) {
		if (CollectionUtils.isEmpty(developerIds)) {
			Set<Long> developerIdsInProject = project.getDevelopers().stream().map(Developer::getId).collect(Collectors.toSet());
			developerIdsInProject.forEach(project::removeDeveloper);
			return project;
		} else {
			Set<Long> developerIdsInProject = project.getDevelopers().stream().map(Developer::getId).collect(Collectors.toSet());
			Set<Long> developerIdsToRemove = developerIdsInProject.stream().filter(developerId -> !developerIds.contains(developerId)).collect(Collectors.toSet());
			developerIdsToRemove.forEach(project::removeDeveloper);
			return addDevelopersToProject(project, developerIds);
		}
	}

	private Project addDevelopersToProject(Project project, Set<Long> developerIds) {
		if (!CollectionUtils.isEmpty(developerIds)) {
			List<Developer> developers = developerRepository.findAllById(developerIds);
			validateIfAllDevelopersAreAvailable(developers, project);
			developers.forEach(project::addDeveloper);
			return project;
		} else {
			return project;
		}
	}

	private void validateIfAllDevelopersAreAvailable(List<Developer> developers, Project project) {
		Optional<Developer> collapsedDeveloper = developers
				.stream()
				.filter(developer -> !developer.isAvailableBetweenExpectProject(project.getId(), project.getStart(), project.getFinish())).findFirst();
		if (collapsedDeveloper.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Developer with id " + collapsedDeveloper.get().getId() + " not available!");
		}
	}

	private static Project createProjectFromRequest(ProjectRequest projectRequest) {
		return Project.builder()
				.name(projectRequest.name())
				.start(projectRequest.start())
				.finish(projectRequest.finish())
				.developers(new HashSet<>())
				.build();
	}
}
