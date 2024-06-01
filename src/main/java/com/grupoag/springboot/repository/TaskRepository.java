package com.grupoag.springboot.repository;

import com.grupoag.springboot.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
