package com.grupoag.springboot.config;

import com.grupoag.springboot.service.ConfigPathsService;
import com.grupoag.springboot.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final ConfigPathsService configPathsService;
    

    /**
     * BasicAuthenticationFilter
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginPageGeneratingFilter
     * DefaultLogoutPageGeneratingFilter
     * FilterSecurityInterceptor
     * Authentication -> Authorization
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//                csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                for (ConfigPaths configMock : configPathsService.generateMatchers()) {
                    http.authorizeRequests()
                            .antMatchers(configMock.getMethod(), configMock.getPath()).hasRole(configMock.getRole());
                }

                http.authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {}", passwordEncoder.encode("1234"));

        auth.inMemoryAuthentication()
                .withUser("alexma")
                .password(passwordEncoder.encode("1234"))
                .roles(configPathsService.generateMatchers().stream().map(c -> c.getRole()).toArray(String[]::new))
                .and()
                .withUser("operator")
                .password(passwordEncoder.encode("1234"))
                .roles("USER_GET", "PROJECT_GET", "PROJECT_GET");

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
