package com.grupoag.springboot.util;

import com.grupoag.springboot.domain.Team;
import com.grupoag.springboot.domain.User;

import java.util.Set;

public class TeamCreator {

    public static Team createTeamToBeSaved(){
        return Team.builder()
                .name("Grupo 1")
                .build();
    }

    public static Team createTeamToBeSaved(Set<User> users){
        return Team.builder()
                .name("Grupo 1")
                .build();
    }
}
