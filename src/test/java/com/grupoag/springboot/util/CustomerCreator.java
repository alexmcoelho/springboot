package com.grupoag.springboot.util;

import com.grupoag.springboot.domain.Customer;

import java.util.HashSet;
import java.util.Set;

public class CustomerCreator {

    public static Customer createCustomerToBeSaved(){
        Set<String> telefones = new HashSet<>();
        telefones.add("35333333");
        telefones.add("35331111");
        return Customer.builder()
                .name("Marcos")
                .email("marcos@gmail.com")
                .register("12345678912")
                .telefones(telefones)
                .build();
    }

}
