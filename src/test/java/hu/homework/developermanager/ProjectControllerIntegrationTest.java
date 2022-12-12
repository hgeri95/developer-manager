package hu.homework.developermanager;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.DeveloperWithProjectsDTO;
import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.dto.ProjectWithDevelopersDTO;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerIntegrationTest {


	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void testProjectCRUD() {
		var createdProject = createProject();
		assert createdProject != null;

		var invalidProjectCreationResponse = createInvalidProject();
		assert invalidProjectCreationResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST);

		var projectById = getProject(createdProject.id()).getBody();
		assert projectById != null;

		var developer = createDeveloper();
		updateProject(createdProject.id(), new ProjectRequest("updated project", LocalDate.now(), LocalDate.now().plusDays(10), Set.of(developer.id())));
		var updatedProject = getProject(createdProject.id()).getBody();
		assert updatedProject.name().equals("updated project");
		assert updatedProject.developers().size() == 1;

		deleteProject(createdProject.id());
		assert getProject(createdProject.id()).getStatusCode().equals(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeveloperProjectRelationship() {
		var developer = createDeveloper();
		var project = createProject();
		updateProject(project.id(),  new ProjectRequest("updated project", LocalDate.now(), LocalDate.now().plusDays(10), Set.of(developer.id())));
		deleteDeveloper(developer.id());
		project = getProject(project.id()).getBody();
		assert project.developers().size() == 0;
	}

	private void deleteProject(Long id) {
		restTemplate.delete(getBaseUrl() + "project/" + id);
	}

	private ProjectWithDevelopersDTO createProject() {
		return this.restTemplate
				.postForEntity(getBaseUrl() + "project", new ProjectRequest("project", LocalDate.now(), LocalDate.now().plusDays(10), null), ProjectWithDevelopersDTO.class).getBody();
	}

	private ResponseEntity<ProjectWithDevelopersDTO> createInvalidProject() {
		return this.restTemplate
				.postForEntity(getBaseUrl() + "project", new ProjectRequest("", LocalDate.now(), LocalDate.now().plusDays(10), null), ProjectWithDevelopersDTO.class);
	}

	private ResponseEntity<ProjectWithDevelopersDTO> getProject(Long id) {
		return this.restTemplate
				.getForEntity(getBaseUrl() + "project/" + id, ProjectWithDevelopersDTO.class);
	}

	private void updateProject(Long id, ProjectRequest projectRequest) {
		this.restTemplate
				.put(
						getBaseUrl() + "project/" + id,
						projectRequest);
	}

	private DeveloperWithProjectsDTO createDeveloper() {
		return this.restTemplate
				.postForEntity(getBaseUrl() + "developer", new DeveloperRequest("name", 23, Set.of(new Skill(Technology.JAVA, 10))), DeveloperWithProjectsDTO.class).getBody();
	}

	private void deleteDeveloper(Long id) {
		restTemplate.delete(getBaseUrl() + "developer/" + id);
	}

	private String getBaseUrl() {
		return "http://localhost:" + port + "/";
	}
}
