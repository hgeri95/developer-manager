package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StringUtils;

import java.util.Set;

@DataJpaTest
@Import({TestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeveloperServiceCRUDTest {

	@Autowired
	private DeveloperService developerService;

	@Test
	@Order(1)
	@Rollback(false)
	public void testCreateDevelopers() {
		var firstDeveloper = developerService.createDeveloper(new DeveloperRequest("test developer", 22, null));
		assert firstDeveloper != null;
		var secondDeveloper = developerService.createDeveloper(new DeveloperRequest("test developer", 22, Set.of(new Skill(Technology.ANDROID, 2))));
		assert secondDeveloper != null;
		assert StringUtils.hasText(secondDeveloper.name());
		assert secondDeveloper.age() != null;
		assert secondDeveloper.skills().size() == 1;
	}

	@Test
	@Order(2)
	public void testReadDevelopers() {
		var developers = developerService.getAllDevelopers();
		assert developers.size() == 2;
		var developer = developerService.getDeveloperById(developers.get(1).id());
		assert developer != null;
	}

	@Test
	@Order(3)
	@Rollback(false)
	public void testUpdateDeveloper() {
		var developers = developerService.getAllDevelopers();
		var developerToUpdate = developers.get(0);

		final String newName = "updated name";
		var updatedDeveloper = developerService
				.updateDeveloper(developerToUpdate.id(), new DeveloperRequest(newName, 22, Set.of(new Skill(Technology.JAVA, 3))));

		assert updatedDeveloper != null;
		assert updatedDeveloper.skills().size() == 1;
		assert updatedDeveloper.name().equals(newName);
	}

	@Test
	@Order(4)
	public void testDeleteDeveloper() {
		var developers = developerService.getAllDevelopers();
		assert developers.size() == 2;

		developerService.deleteDeveloper(developers.get(1).id());
		developers = developerService.getAllDevelopers();
		assert developers.size() == 1;
	}
}
