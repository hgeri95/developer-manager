package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.mapper.ProjectMapperImpl;
import hu.homework.developermanager.repository.DeveloperRepository;
import hu.homework.developermanager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

	private ProjectRepository projectRepository = mock(ProjectRepository.class);
	private DeveloperRepository developerRepository = mock(DeveloperRepository.class);
	private ProjectService projectService = new ProjectService(projectRepository, developerRepository, new ProjectMapperImpl());

	@BeforeEach
	public void init() {
		when(projectRepository.findById(1L)).thenReturn(Optional.empty());
	}


	@Test
	public void testRemoveNotExistingProject() {
		assertThrows(ResponseStatusException.class, () -> projectService.deleteProject(1L));
	}

	@Test
	public void testUpdateNotExistingProject() {
		assertThrows(ResponseStatusException.class, () -> projectService.updateProject(1L, new ProjectRequest("name", LocalDate.MIN, LocalDate.MAX, null)));
	}

	@Test
	public void testCreateProjectWithInvalidDates() {
		assertThrows(ResponseStatusException.class, () -> projectService.createProject(new ProjectRequest("name",  LocalDate.MAX, LocalDate.MIN,null)));
	}

	@Test
	public void testUpdateProjectWithInvalidDates() {
		assertThrows(ResponseStatusException.class, () -> projectService.updateProject(2L, new ProjectRequest("name",  LocalDate.MAX, LocalDate.MIN,null)));
	}
}
