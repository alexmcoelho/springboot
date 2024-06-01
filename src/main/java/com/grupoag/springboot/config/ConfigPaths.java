package com.grupoag.springboot.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigPaths {

    private String path;
    private HttpMethod method;
    private String role;

}
