package com.grupoag.springboot.integration;

import com.grupoag.springboot.domain.Customer;
import com.grupoag.springboot.repository.CustomerRepository;
import com.grupoag.springboot.util.CustomerCreator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateWithRoles")
    private TestRestTemplate testRestTemplateWithRoles;
    @Autowired
    private CustomerRepository customerRepository;

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
    @DisplayName("list returns list of customer inside page object when successful")
    void list_ReturnsListOfCustomersInsidePageObject_WhenSuccessful() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        String expectedName = savedCustomer.getName();

        PageableResponse<Customer> customerPage = testRestTemplateWithRoles.exchange("/customer", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Customer>>() {
                }).getBody();

        Assertions.assertThat(customerPage).isNotNull();

        Assertions.assertThat(customerPage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(customerPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of customer when successful")
    void listAll_ReturnsListOfCustomers_WhenSuccessful() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        String expectedName = savedCustomer.getName();

        List<Customer> customer = testRestTemplateWithRoles.exchange("/customer/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Customer>>() {
                }).getBody();

        Assertions.assertThat(customer)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(customer.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns customer when successful")
    void findById_ReturnsCustomer_WhenSuccessful() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        Long expectedId = savedCustomer.getId();

        Customer customer = testRestTemplateWithRoles.getForObject("/customer/{id}", Customer.class, expectedId);

        Assertions.assertThat(customer).isNotNull();

        Assertions.assertThat(customer.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("save returns customer when successful")
    void save_ReturnsCustomer_WhenSuccessful() {

        Set<String> telefones = new HashSet<>();
        telefones.add("35333333");
        telefones.add("35331111");
        
        Customer customerPostRequestBody = CustomerCreator.createCustomerToBeSaved();

        ResponseEntity<Customer> customerResponseEntity = testRestTemplateWithRoles.postForEntity("/customer", customerPostRequestBody, Customer.class);

        Assertions.assertThat(customerResponseEntity).isNotNull();
        Assertions.assertThat(customerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(customerResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(customerResponseEntity.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace updates customer when successful")
    void replace_UpdatesCustomer_WhenSuccessful() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        savedCustomer.setName("new name");

        ResponseEntity<Void> customerResponseEntity = testRestTemplateWithRoles.exchange("/customer/{id}",
                HttpMethod.PUT, new HttpEntity<>(savedCustomer), Void.class, savedCustomer.getId());

        Assertions.assertThat(customerResponseEntity).isNotNull();

        Assertions.assertThat(customerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes customer when successful")
    void delete_RemovesCustomer_WhenSuccessful() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        ResponseEntity<Void> customerResponseEntity = testRestTemplateWithRoles.exchange("/customer/{id}",
                HttpMethod.DELETE, null, Void.class, savedCustomer.getId());

        Assertions.assertThat(customerResponseEntity).isNotNull();

        Assertions.assertThat(customerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Customer savedCustomer = customerRepository.save(CustomerCreator.createCustomerToBeSaved());

        ResponseEntity<Void> customerResponseEntity = testRestTemplateRoleUser.exchange("/customer/{id}",
                HttpMethod.DELETE, null, Void.class, savedCustomer.getId());

        Assertions.assertThat(customerResponseEntity).isNotNull();

        Assertions.assertThat(customerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
