package com.grupoag.springboot.util;

import com.grupoag.springboot.domain.Customer;
import com.grupoag.springboot.domain.Project;
import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.domain.enums.ProjectStatus;

import java.util.Date;
import java.util.Set;

public class ProjectCreator {

    public static Project createProjectToBeSaved(Customer customer, Set<Team> teams){
        return Project.builder()
                .name("Primeiro projeto")
                .projectStatus(ProjectStatus.OPEN)
                .startDate(new Date())
                .customer(customer)
                .teams(teams)
                .build();
    }

    public static Project createProjectToBeSaved(Customer customer){
        return Project.builder()
                .name("Primeiro projeto")
                .projectStatus(ProjectStatus.OPEN)
                .startDate(new Date())
                .customer(customer)
                .build();
    }
}
