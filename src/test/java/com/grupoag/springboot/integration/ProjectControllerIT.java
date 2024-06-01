package com.grupoag.springboot.integration;

import com.grupoag.springboot.domain.*;
import com.grupoag.springboot.exception.BadRequestException;
import com.grupoag.springboot.repository.*;
import com.grupoag.springboot.service.ConfigPathsService;
import com.grupoag.springboot.util.CustomerCreator;
import com.grupoag.springboot.util.ProjectCreator;
import com.grupoag.springboot.util.TaskCreator;
import com.grupoag.springboot.util.TeamCreator;
import com.grupoag.springboot.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProjectControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateWithRoles")
    private TestRestTemplate testRestTemplateWithRoles;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfigPathsService configPathsService;
    @Autowired
    private TaskRepository taskRepository;

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

    public User createUser() {
        return User.builder()
                .name("User 1")
                .password("{bcrypt}$2a$12$Czjmk6ep6b6exMlaUVGQpOcelDzXsZOAZhtJCfn7g7bZqacf5zb92")
                .username("user1")
                .authorities(String.join(",", configPathsService.generateMatchers("project").stream().map(c -> c.getRole()).toArray(String[]::new)))
                .build();
    }

    @Test
    @DisplayName("create start project with a project when successful")
    void create_StartProjectWithProject_WhenSuccessful() {
        Customer customer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());
        User user = userRepository.save(createUser());
        Set<User> users = new HashSet<>();
        users.add(user);
        Team team = teamRepository.save(TeamCreator.createTeamToBeSaved(users));
        Project savedProject = projectRepository.save(ProjectCreator.createProjectToBeSaved(customer, new HashSet<>(Arrays.asList(team))));

        String expectedName = savedProject.getName();

        PageableResponse<Project> projectPage = testRestTemplateWithRoles.exchange("/project", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Project>>() {
                }).getBody();

        Assertions.assertThat(projectPage).isNotNull();

        Assertions.assertThat(projectPage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(projectPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("create task in project when successful")
    void add_TaskInProject_WhenSuccessful() {
        Customer customer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());
        User user = userRepository.save(createUser());
        Set<User> users = new HashSet<>();
        users.add(user);
        Project savedProject = projectRepository.save(ProjectCreator.createProjectToBeSaved(customer));

        ResponseEntity<Void> customerResponseEntity = testRestTemplateWithRoles.exchange("/project/{id}/task",
                HttpMethod.PUT, new HttpEntity<>(TaskCreator.createTaskToBeSaved(savedProject)), Void.class, savedProject.getId());

        Task register = taskRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("Project not Found"));

        Assertions.assertThat(register.getProject()).isNotNull();
    }


}
