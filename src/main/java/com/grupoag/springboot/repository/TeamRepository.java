package com.grupoag.springboot.repository;

import com.grupoag.springboot.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Long> {

}
