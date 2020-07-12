package com.cksutil.sassyboot.auth.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Arrays;

@Configuration
@ConfigurationProperties(prefix = "app.security")
@EnableWebSecurity(debug = false)
@Setter
public class AppSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private String csrfTokenHeaderName;
    private String csrfTokenCookieName;
    private String authTokenHeaderName;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**").csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .requireCsrfProtectionMatcher(AnyRequestMatcher.INSTANCE)
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/public/**"))
                .and()
                .requestCache().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/session").permitAll()
                .antMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .addFilterBefore(sbCustomAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/session", HttpMethod.DELETE.name()))
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint( (request, response, authException) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setDateFormat(new StdDateFormat());
        return objectMapper;
    }

    @Bean
    public UsernamePasswordAuthenticationFilter sbCustomAuthFilter() throws Exception {
        UsernamePasswordAuthenticationFilter authenticationFilter = new JSONBodyUsernamePasswordAuthFilter(defaultObjectMapper());
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/session", HttpMethod.POST.name()));
        authenticationFilter.setUsernameParameter("username");
        authenticationFilter.setPasswordParameter("password");
        authenticationFilter.setAuthenticationManager(authenticationManager());

        final AuthenticationHandler authenticationHandler = new AuthenticationHandler(defaultObjectMapper());

        authenticationFilter.setAuthenticationSuccessHandler(authenticationHandler);
        authenticationFilter.setAuthenticationFailureHandler(authenticationHandler);

        authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        return authenticationFilter;
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(
                new ChangeSessionIdAuthenticationStrategy(),
                new CsrfAuthenticationStrategy(csrfTokenRepository())
        ));
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new AppCsrfTokenRepository(csrfTokenCookieName,csrfTokenHeaderName);
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new AppSessionIdResolver(authTokenHeaderName);
    }
}