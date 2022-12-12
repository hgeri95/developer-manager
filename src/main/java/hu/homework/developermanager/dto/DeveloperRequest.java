package hu.homework.developermanager.dto;

import hu.homework.developermanager.model.Skill;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public record DeveloperRequest(
		@NotBlank(message = "Name cannot be empty") String name,
		@NotNull(message = "Age cannot be empty") @Min(value = 1, message = "Age cannot be negative") Integer age,
		@Valid Set<Skill> skills) {}
