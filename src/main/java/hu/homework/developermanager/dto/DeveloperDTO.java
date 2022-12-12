package hu.homework.developermanager.dto;

import hu.homework.developermanager.model.Skill;

import java.util.Set;

public record DeveloperDTO(Long id, String name, Integer age, Set<Skill> skills) {
}
