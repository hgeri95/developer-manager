package hu.homework.developermanager.dto;

import hu.homework.developermanager.model.Skill;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Set;

public record DeveloperFilterRequest(LocalDate projectStart, LocalDate projectFinish, @Valid Set<Skill> requiredSkills) {
}
