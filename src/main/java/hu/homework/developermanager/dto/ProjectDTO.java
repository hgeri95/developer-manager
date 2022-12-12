package hu.homework.developermanager.dto;

import java.time.LocalDate;

public record ProjectDTO(Long id, String name, LocalDate start, LocalDate finish) {
}
