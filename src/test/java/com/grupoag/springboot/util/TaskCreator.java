package com.grupoag.springboot.util;

import com.grupoag.springboot.domain.Project;
import com.grupoag.springboot.domain.Task;
import com.grupoag.springboot.domain.User;

import java.util.Set;

public class TaskCreator {

    public static Task createTaskToBeSaved(Project project){
        return Task.builder()
                .name("Atividade 1")
                .observation("tenha atenção")
//                .project(project)
//                .id(1L)
                .build();
    }

}
