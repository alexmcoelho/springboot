package com.grupoag.springboot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupoag.springboot.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private ProjectStatus projectStatus;
    @JsonFormat(pattern="dd/MM/yyyy HH:mm", locale = "pt-BR", timezone = "Brazil/East")
    private Date startDate;
    @JsonFormat(pattern="dd/MM/yyyy HH:mm", locale = "pt-BR", timezone = "Brazil/East")
    private Date entDate;
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TEAM_PROJECT_MAPPING", joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
    private Set<Task> tasks = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
