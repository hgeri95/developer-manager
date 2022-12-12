package hu.homework.developermanager;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.DeveloperWithProjectsDTO;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeveloperControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void testDeveloperCRUD() {
		var createdDeveloper = createDeveloper();
		assert createdDeveloper != null;

		var invalidDeveloperCreationResponse = createInvalidDeveloper();
		assert invalidDeveloperCreationResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST);

		var developerById = getDeveloper(createdDeveloper.id()).getBody();
		assert developerById != null;

		updateDeveloper(createdDeveloper.id(), new DeveloperRequest("updated name", 10, Set.of(new Skill(Technology.ANDROID, 2))));
		var updatedDeveloper = getDeveloper(createdDeveloper.id()).getBody();
		assert updatedDeveloper.name().equals("updated name");
		assert updatedDeveloper.skills().size() == 1;

		deleteDeveloper(createdDeveloper.id());
		assert getDeveloper(createdDeveloper.id()).getStatusCode().equals(HttpStatus.NOT_FOUND);
	}

	private void deleteDeveloper(Long id) {
		restTemplate.delete(getBaseUrl() + "developer/" + id);
	}

	private DeveloperWithProjectsDTO createDeveloper() {
		return this.restTemplate
				.postForEntity(getBaseUrl() + "developer", new DeveloperRequest("name", 23, null), DeveloperWithProjectsDTO.class).getBody();
	}

	private ResponseEntity<DeveloperWithProjectsDTO> createInvalidDeveloper() {
		return this.restTemplate
				.postForEntity(getBaseUrl() + "developer", new DeveloperRequest("name", -2, null), DeveloperWithProjectsDTO.class);
	}

	private ResponseEntity<DeveloperWithProjectsDTO> getDeveloper(Long id) {
		return this.restTemplate
				.getForEntity(getBaseUrl() + "developer/" + id, DeveloperWithProjectsDTO.class);
	}

	private void updateDeveloper(Long id, DeveloperRequest developerRequest) {
		this.restTemplate
				.put(
						getBaseUrl() + "developer/" + id,
						developerRequest);
	}

	private String getBaseUrl() {
		return "http://localhost:" + port + "/";
	}
}
