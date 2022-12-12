package hu.homework.developermanager.dto;

import java.time.LocalDate;
import java.util.Set;

public record ProjectWithDevelopersDTO(Long id, String name, LocalDate start, LocalDate finish, Set<DeveloperDTO> developers) {
}
