package com.grupoag.springboot.service;

import com.grupoag.springboot.domain.Project;
import com.grupoag.springboot.domain.Task;
import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.domain.User;
import com.grupoag.springboot.exception.BadRequestException;
import com.grupoag.springboot.repository.ProjectRepository;
import com.grupoag.springboot.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public Project newProject(Project project) {
        Project project1 = new Project();
        project1.setId(project.getId());
        project1.setName(project.getName());
        return project1;
    }

    public Page<Project> listAll(Pageable pageable) {
        Page<Project> list = projectRepository.findAll(pageable);
        Page<Project> dtoPage = list.map(project -> newProject(project));
        return dtoPage;
    }

    public List<Project> listAllNonPageable() {
        return projectRepository.findAll();
    }

    public Project findByIdOrThrowBadRequestException(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Project not Found"));
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void delete(long id) {
        projectRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void update(Project project) {
        Project savedProject = findByIdOrThrowBadRequestException(project.getId());
        project.setId(savedProject.getId());
        projectRepository.save(project);
    }

    @Transactional
    public Task addTask(Long id, Task task) {
        Project savedProject = findByIdOrThrowBadRequestException(id);

        task.setProject(savedProject);
        Task test = taskRepository.save(task);
        return test;
    }

    @Transactional
    public Project addTeam(Long id, Team task) {
        Project savedProject = findByIdOrThrowBadRequestException(id);

        Set<Team> teams = new HashSet<>();
        if(savedProject.getTeams() == null) {
           savedProject.setTeams(teams);
        }
        else {
            teams = savedProject.getTeams();
        }
        teams.add(task);
        savedProject.setTeams(teams);
        Project test = projectRepository.save(savedProject);
        return test;
    }
}
