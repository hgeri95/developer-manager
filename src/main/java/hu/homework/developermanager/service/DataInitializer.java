package hu.homework.developermanager.service;

import hu.homework.developermanager.dto.DeveloperRequest;
import hu.homework.developermanager.dto.ProjectRequest;
import hu.homework.developermanager.model.Skill;
import hu.homework.developermanager.model.Technology;
import hu.homework.developermanager.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile(value = "local")
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final DeveloperService developerService;
	private final DeveloperRepository developerRepository;
	private final ProjectService projectService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		initTestData();
	}

	@Transactional
	private void initTestData() {
		log.info("Create test data");
		if (developerService.getAllDevelopers().isEmpty()) {
			developerService.createDeveloper(
					new DeveloperRequest("Kiss Béla", 24, Set.of(new Skill(Technology.JAVA, 4), new Skill(Technology.ANDROID, 2))));
			developerService.createDeveloper(
					new DeveloperRequest("Nagy András", 32, Set.of(new Skill(Technology.ANGULAR, 10))));
		}
		if (projectService.getAllProjects().isEmpty()) {
			var developers = developerRepository.findAll();
			projectService.createProject(
					new ProjectRequest("BKK app", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 8, 10), Set.of(developers.get(0).getId())));
			projectService.createProject(
					new ProjectRequest("MOL app", LocalDate.of(2022, 10, 1), LocalDate.of(2023, 3, 10), Set.of(developers.get(1).getId())));
		}
		log.info("Test data created");
	}
}
