package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import hu.homework.developermanager.model.entity.Project;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Set;

@DataJpaTest
@Import({TestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectServiceCRUDTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private DeveloperService developerService;

	@Test
	@Order(1)
	@Rollback(false)
	public void testCreateProject() {
		generateDevelopers();
		var firstProject = projectService.createProject(new ProjectRequest("test project", LocalDate.MIN, LocalDate.MAX, null));
		assert firstProject != null;

		final Long developerId = developerService.getAllDevelopers().get(1).id();
		var secondProject = projectService.createProject(new ProjectRequest("second project", LocalDate.MIN, LocalDate.MAX, Set.of(developerId)));
		assert secondProject != null;
		assert StringUtils.hasText(secondProject.name());
		assert secondProject.start() != null;
		assert secondProject.finish() != null;
		assert secondProject.developers().size() == 1;
	}

	@Test
	@Order(2)
	public void testReadProjects() {
		var projects = projectService.getAllProjects();
		assert projects.size() == 2;

		var project = projectService.getProjectById(projects.get(1).id());
		assert project != null;
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void testUpdateProject() {
		var project = projectService.getAllProjects().get(1);
		var developer = developerService.getAllDevelopers().get(1);

		final String newProjectName = "new project name";
		var updatedProject = projectService.updateProject(project.id(), new ProjectRequest(newProjectName, project.start(), project.finish(), Set.of(developer.id())));
		assert updatedProject != null;
		assert updatedProject.name().equals(newProjectName);
		assert updatedProject.developers().size() == 1;
	}

	@Test
	@Order(4)
	public void testRemoveProject() {
		var projectId = projectService.getAllProjects().get(1).id();
		var project = projectService.getProjectById(projectId);

		assert project.developers().size() == 1;
		final Long developerId = project.developers().iterator().next().id();
		assert developerService.getDeveloperById(developerId).projects().size() == 1;

		projectService.deleteProject(projectId);
		assert projectService.getAllProjects().size() == 1;
		assert developerService.getDeveloperById(developerId).projects().size() == 0;
	}

	private void generateDevelopers() {
		developerService.createDeveloper(new DeveloperRequest("developer1", 30, null));
		developerService.createDeveloper(new DeveloperRequest("developer2", 40, Set.of(new Skill(Technology.ANDROID, 1))));
	}
}
