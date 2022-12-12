package hu.homework.developermanager.service;

import hu.homework.developermanager.mapper.DeveloperMapper;
import hu.homework.developermanager.mapper.DeveloperMapperImpl;
import hu.homework.developermanager.mapper.ProjectMapper;
import hu.homework.developermanager.mapper.ProjectMapperImpl;
import hu.homework.developermanager.repository.DeveloperRepository;
import hu.homework.developermanager.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

	@Autowired
	private DeveloperRepository developerRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Bean
	public DeveloperMapper developerMapper() {
		return new DeveloperMapperImpl();
	}

	@Bean
	public DeveloperService developerService() {
		return new DeveloperService(developerRepository, developerMapper());
	}

	@Bean
	public ProjectMapper projectMapper() {
		return new ProjectMapperImpl();
	}

	@Bean
	public ProjectService projectService() {
		return new ProjectService(projectRepository, developerRepository, projectMapper());
	}
}
