package hu.homework.developermanager.mapper;

import hu.homework.developermanager.dto.DeveloperWithProjectsDTO;
import hu.homework.developermanager.model.entity.Developer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
	DeveloperWithProjectsDTO entityToDTO(Developer developer);
}
