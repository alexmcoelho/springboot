package com.grupoag.springboot.integration;

import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.repository.TeamRepository;
import com.grupoag.springboot.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.grupoag.springboot.util.TeamCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TeamControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateWithRoles")
    private TestRestTemplate testRestTemplateWithRoles;
    @Autowired
    private TeamRepository teamRepository;

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("operator", "1234");
            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean(name = "testRestTemplateWithRoles")
        public TestRestTemplate testRestTemplateWithRolesCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("alexma", "1234");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list returns list of team inside page object when successful")
    void list_ReturnsListOfTeamsInsidePageObject_WhenSuccessful() {
        Team savedTeam = teamRepository.save(com.grupoag.springboot.util.TeamCreator.createTeamToBeSaved());

        String expectedName = savedTeam.getName();

        PageableResponse<Team> teamPage = testRestTemplateWithRoles.exchange("/team", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Team>>() {
                }).getBody();

        Assertions.assertThat(teamPage).isNotNull();

        Assertions.assertThat(teamPage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(teamPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of team when successful")
    void listAll_ReturnsListOfTeams_WhenSuccessful() {
        Team savedTeam = teamRepository.save(TeamCreator.createTeamToBeSaved());

        String expectedName = savedTeam.getName();

        List<Team> team = testRestTemplateWithRoles.exchange("/team/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Team>>() {
                }).getBody();

        Assertions.assertThat(team)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(team.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns team when successful")
    void findById_ReturnsTeam_WhenSuccessful() {
        Team savedTeam = teamRepository.save(TeamCreator.createTeamToBeSaved());

        Long expectedId = savedTeam.getId();

        Team team = testRestTemplateWithRoles.getForObject("/team/{id}", Team.class, expectedId);

        Assertions.assertThat(team).isNotNull();

        Assertions.assertThat(team.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("save returns team when successful")
    void save_ReturnsTeam_WhenSuccessful() {

        Set<String> telefones = new HashSet<>();
        telefones.add("35333333");
        telefones.add("35331111");
        
        Team teamPostRequestBody = TeamCreator.createTeamToBeSaved();

        ResponseEntity<Team> teamResponseEntity = testRestTemplateWithRoles.postForEntity("/team", teamPostRequestBody, Team.class);

        Assertions.assertThat(teamResponseEntity).isNotNull();
        Assertions.assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(teamResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(teamResponseEntity.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace updates team when successful")
    void replace_UpdatesTeam_WhenSuccessful() {
        Team savedTeam = teamRepository.save(TeamCreator.createTeamToBeSaved());

        savedTeam.setName("new name");

        ResponseEntity<Void> teamResponseEntity = testRestTemplateWithRoles.exchange("/team/{id}",
                HttpMethod.PUT, new HttpEntity<>(savedTeam), Void.class, savedTeam.getId());

        Assertions.assertThat(teamResponseEntity).isNotNull();

        Assertions.assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes team when successful")
    void delete_RemovesTeam_WhenSuccessful() {
        Team savedTeam = teamRepository.save(TeamCreator.createTeamToBeSaved());

        ResponseEntity<Void> teamResponseEntity = testRestTemplateWithRoles.exchange("/team/{id}",
                HttpMethod.DELETE, null, Void.class, savedTeam.getId());

        Assertions.assertThat(teamResponseEntity).isNotNull();

        Assertions.assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Team savedTeam = teamRepository.save(TeamCreator.createTeamToBeSaved());

        ResponseEntity<Void> teamResponseEntity = testRestTemplateRoleUser.exchange("/team/{id}",
                HttpMethod.DELETE, null, Void.class, savedTeam.getId());

        Assertions.assertThat(teamResponseEntity).isNotNull();

        Assertions.assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
