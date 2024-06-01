package com.grupoag.springboot.controller;

import com.grupoag.springboot.domain.Customer;
import com.grupoag.springboot.service.CustomerService;
import com.mysql.cj.log.Log;
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
@RequestMapping("customer")
@Log4j2
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<Customer>> list(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(customerService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Customer>> listAll() {
        return ResponseEntity.ok(customerService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Customer> findById(@PathVariable long id) {
        return ResponseEntity.ok(customerService.findByIdOrThrowBadRequestException(id));
    }

    @GetMapping(path = "by-id/{id}")
    public ResponseEntity<Customer> findByIdAuthenticationPrincipal(@PathVariable long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails);
        return ResponseEntity.ok(customerService.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Customer> save(@RequestBody @Valid Customer customerPostRequestBody) {
        return new ResponseEntity<>(customerService.save(customerPostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When Customer Does Not Exist in The Database")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {
        customerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> replace(@PathVariable Long id, @RequestBody Customer customerPutRequestBody) {
        if(id != null) {
            customerPutRequestBody.setId(id);
        }
        customerService.update(customerPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
