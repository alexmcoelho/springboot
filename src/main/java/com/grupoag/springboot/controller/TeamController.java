package com.grupoag.springboot.controller;

import com.grupoag.springboot.service.TeamService;
import com.grupoag.springboot.domain.Team;
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
@RequestMapping("team")
@Log4j2
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<Page<Team>> list(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(teamService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Team>> listAll() {
        return ResponseEntity.ok(teamService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Team> findById(@PathVariable long id) {
        return ResponseEntity.ok(teamService.findByIdOrThrowBadRequestException(id));
    }

    @GetMapping(path = "by-id/{id}")
    public ResponseEntity<Team> findByIdAuthenticationPrincipal(@PathVariable long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails);
        return ResponseEntity.ok(teamService.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Team> save(@RequestBody @Valid Team teamPostRequestBody) {
        return new ResponseEntity<>(teamService.save(teamPostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When Team Does Not Exist in The Database")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {
        teamService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> replace(@PathVariable Long id, @RequestBody Team teamPutRequestBody) {
        if(id != null) {
            teamPutRequestBody.setId(id);
        }
        teamService.update(teamPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
