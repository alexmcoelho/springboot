package com.grupoag.springboot.controller;

import com.grupoag.springboot.domain.Task;
import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.service.ProjectService;
import com.grupoag.springboot.domain.Project;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("project")
@Log4j2
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<Project>> list(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(projectService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Project>> listAll() {
        return ResponseEntity.ok(projectService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Project> findById(@PathVariable long id) {
        return ResponseEntity.ok(projectService.findByIdOrThrowBadRequestException(id));
    }

    @GetMapping(path = "by-id/{id}")
    public ResponseEntity<Project> findByIdAuthenticationPrincipal(@PathVariable long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails);
        return ResponseEntity.ok(projectService.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Project> save(@RequestBody @Valid Project projectPostRequestBody) {
        return new ResponseEntity<>(projectService.save(projectPostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When Project Does Not Exist in The Database")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {
        projectService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> replace(@PathVariable Long id, @RequestBody Project projectPutRequestBody) {
        if(id != null) {
            projectPutRequestBody.setId(id);
        }
        projectService.update(projectPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}/task")
    public ResponseEntity<Void> addTask(@PathVariable Long id, @RequestBody Task task) {
        projectService.addTask(id, task);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}/team")
    public ResponseEntity<Void> addTeam(@PathVariable Long id, @RequestBody Team team) {
        projectService.addTeam(id, team);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
