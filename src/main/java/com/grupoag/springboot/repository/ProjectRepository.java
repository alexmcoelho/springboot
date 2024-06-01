package com.grupoag.springboot.repository;

import com.grupoag.springboot.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Transactional(readOnly=true)
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :projectId")
    List<Project> findByProjectId(@Param("projectId") Long projectId);
}
