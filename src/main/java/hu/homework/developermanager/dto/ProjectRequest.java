package hu.homework.developermanager.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public record ProjectRequest(
		@NotBlank(message = "Name cannot be empty") String name,
		@NotNull(message = "Start cannot be empty") LocalDate start,
		@NotNull(message = "Finish cannot be empty") LocalDate finish,
		Set<Long> developerIds) {}
