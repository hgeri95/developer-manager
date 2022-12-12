package hu.homework.developermanager.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import hu.homework.developermanager.model.Skill;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "developer")
@TypeDefs(@TypeDef(name = "json", typeClass = JsonType.class))
public class Developer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	private int age;

	@Type(type = "json")
	@Column(columnDefinition = "json")
	Set<Skill> skills;

	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			},
			mappedBy = "developers")
	@Setter(AccessLevel.NONE)
	Set<Project> projects = new HashSet<>();

	public boolean isAvailableBetween(LocalDate start, LocalDate finish) {
		return this.projects.stream().noneMatch(project -> isProjectOverlaps(project, start, finish));
	}

	public boolean isAvailableBetweenExpectProject(Long projectId, LocalDate start, LocalDate finish) {
		return this.projects.stream().noneMatch(project -> !project.getId().equals(projectId) && isProjectOverlaps(project, start, finish));
	}

	public boolean hasRequiredSkills(Set<Skill> requiredSkills) {
		if (CollectionUtils.isEmpty(requiredSkills)) {
			return true;
		} else {
			return requiredSkills
					.stream()
					.allMatch(requiredSkill ->
							skills.stream().anyMatch(skill -> skill.technology().equals(requiredSkill.technology())
									&& skill.yearsOfExperience() >= requiredSkill.yearsOfExperience()));
		}
	}

	private static boolean isProjectOverlaps(Project project,LocalDate start, LocalDate finish) {
		return start.isBefore(project.getFinish()) && finish.isAfter(project.getStart());
	}
}
