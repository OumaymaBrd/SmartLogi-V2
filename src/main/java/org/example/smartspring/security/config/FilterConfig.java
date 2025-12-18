package org.example.smartspring.security.config;

import org.example.smartspring.security.filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> registration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        // Désactive l'enregistrement automatique comme filtre Servlet standard
        // Le filtre sera utilisé UNIQUEMENT par Spring Security
        registration.setEnabled(false);
        return registration;
    }
}