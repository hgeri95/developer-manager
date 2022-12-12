package hu.homework.developermanager.model;

import javax.validation.constraints.NotNull;

public record Skill(
		@NotNull(message = "Technology cannot be empty") Technology technology,
		@NotNull(message = "Years of experience cannot be empty") Integer yearsOfExperience) {
}
