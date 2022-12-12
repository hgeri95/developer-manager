package hu.homework.developermanager.mapper;

import hu.homework.developermanager.dto.ProjectWithDevelopersDTO;
import hu.homework.developermanager.model.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
	ProjectWithDevelopersDTO entityToDTO(Project project);
}
