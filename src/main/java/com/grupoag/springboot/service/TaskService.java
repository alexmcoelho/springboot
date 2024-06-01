package com.grupoag.springboot.service;

import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.exception.BadRequestException;
import com.grupoag.springboot.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TeamRepository teamRepository;

    public Page<Team> listAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public List<Team> listAllNonPageable() {
        return teamRepository.findAll();
    }

    public Team findByIdOrThrowBadRequestException(long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Team not Found"));
    }

    @Transactional
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public void delete(long id) {
        teamRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void update(Team team) {
        Team savedTeam = findByIdOrThrowBadRequestException(team.getId());
        team.setId(savedTeam.getId());
        teamRepository.save(team);
    }
}
