package hu.homework.developermanager.dto;

import hu.homework.developermanager.model.Skill;

import java.util.Set;

public record DeveloperWithProjectsDTO(Long id, String name, Integer age, Set<Skill> skills, Set<ProjectDTO> projects) {
}
