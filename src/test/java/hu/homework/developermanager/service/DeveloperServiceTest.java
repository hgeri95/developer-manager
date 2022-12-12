package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.DeveloperFilterRequest;
import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.mapper.DeveloperMapperImpl;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import hu.homework.developermanager.model.entity.Developer;
import hu.homework.developermanager.model.entity.Project;
import hu.homework.developermanager.repository.DeveloperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeveloperServiceTest {

	private DeveloperRepository developerRepository = mock(DeveloperRepository.class);

	private DeveloperService developerService = new DeveloperService(developerRepository, new DeveloperMapperImpl());

	@BeforeEach
	public void init() {
		when(developerRepository.findById(1L)).thenReturn(Optional.empty());
	}

	@Test
	public void testRemoveNotExistingDeveloper() {

		assertThrows(ResponseStatusException.class, () -> developerService.deleteDeveloper(1L));
	}

	@Test
	public void updateNotExistingDeveloper() {

		assertThrows(ResponseStatusException.class, () -> developerService.updateDeveloper(1L, new DeveloperRequest("name", 21, null)));
	}

	@Test
	public void testDeveloperFilteringInvalidRequest() {
		assertThrows(ResponseStatusException.class, () -> developerService.filterDevelopers(new DeveloperFilterRequest(LocalDate.MAX, null, null)));
	}

	@Test
	public void testDeveloperFiltering() {
		mockDevelopers();
		var filteredDevelopers = developerService
				.filterDevelopers(new DeveloperFilterRequest(
						LocalDate.of(2023, 3, 10),
						LocalDate.of(2023, 10, 10),
						Set.of(new Skill(Technology.JAVA, 3))));
		assert filteredDevelopers.size() == 1;
		assert filteredDevelopers.get(0).skills().iterator().next().technology().equals(Technology.JAVA);
	}

	private void mockDevelopers() {
		Project project = new Project(13L, "project", LocalDate.of(2023, 2, 10), LocalDate.of(2023, 5, 20), null);
		Developer developer1 = new Developer(2L, "name2", 30, Set.of(new Skill(Technology.JAVA, 20)), emptySet());
		Developer developer2 = new Developer(3L, "name3", 30, Set.of(new Skill(Technology.JAVA, 20)), Set.of(project));
		Developer developer3 = new Developer(4L, "name4", 30, Set.of(new Skill(Technology.ANDROID, 20)), emptySet());
		when(developerRepository.findAll()).thenReturn(List.of(developer1, developer2, developer3));
	}
}
