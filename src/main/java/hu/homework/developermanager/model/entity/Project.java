package hu.homework.developermanager.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	private LocalDate start;

	@Column
	private LocalDate finish;

	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			})
	@JoinTable(
			name = "project_developer",
			joinColumns = @JoinColumn(name = "project_id"),
			inverseJoinColumns = @JoinColumn(name = "developer_id")
	)
	@Setter(AccessLevel.NONE)
	Set<Developer> developers = new HashSet<>();

	public void addDeveloper(Developer developer) {
		this.developers.add(developer);
		developer.getProjects().add(this);
	}

	public void removeDeveloper(Long developerId) {
		Developer developer = this.developers.stream().filter(d -> d.getId().equals(developerId)).findFirst().orElse(null);
		if (developer != null) {
			this.developers.remove(developer);
			developer.projects.remove(this);
		}
	}
}
