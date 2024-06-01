package com.grupoag.springboot.service;

import com.grupoag.springboot.config.ConfigPaths;
import com.grupoag.springboot.config.EnumHttpStatus;
import com.grupoag.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigPathsService {
    private final UserRepository devDojoUserRepository;

    private static final String[] MATCHERS = {
            "customer",
            "team",
            "project"
    };

    public HttpMethod toExternalEnumHttpStatus(EnumHttpStatus httpStatus) {
        switch (httpStatus) {
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case GET:
                return HttpMethod.GET;
            case DELETE:
                return HttpMethod.DELETE;
        }
        return null;
    }

    public ConfigPaths mapperConfigMock(String path, EnumHttpStatus method){
        String role = path.toUpperCase() + "_" + method.toString();
        HttpMethod httpMethod = toExternalEnumHttpStatus(method);

        return ConfigPaths.builder()
                .path("/" + path + "/**")
                .method(httpMethod)
                .role(role)
                .build();
    }
    public List<ConfigPaths> generateMatchers() {
        List<ConfigPaths> list = new ArrayList<>();

        for (String path : MATCHERS) {
            for (EnumHttpStatus x : EnumHttpStatus.values()) {
                list.add(mapperConfigMock(path, x));
            }
        }
        return list;
    }
    public List<ConfigPaths> generateMatchers(String byPath) {
        List<ConfigPaths> list = new ArrayList<>();

        for (String path : MATCHERS) {
            if(!path.equals((byPath))) {
                continue;
            }
            for (EnumHttpStatus x : EnumHttpStatus.values()) {
                list.add(mapperConfigMock(path, x));
            }
        }
        return list;
    }
}
