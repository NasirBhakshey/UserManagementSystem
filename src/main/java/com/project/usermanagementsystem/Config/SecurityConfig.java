package com.project.usermanagementsystem.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.usermanagementsystem.Filter.JwtAuthenticateFilter;
import com.project.usermanagementsystem.Services.CustomAdminService;
import com.project.usermanagementsystem.Services.CustomManagerService;
import com.project.usermanagementsystem.Services.CustomUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private final JwtAuthenticateFilter jwtAuthFilter;
    @Autowired
    private final CustomUserService userDetailsService;
    @Autowired
    private final CustomAdminService customAdminService;
    @Autowired
    private final CustomManagerService customManagerService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/loginpage", "/api/auth/login-page", "/api/auth/create-user",
                        "/api/auth/reg-page","/api/auth/dashboard","/api/auth/view-users","/api/auth/delete/{id}",
                        "/api/auth/view/delete/{id}","/api/auth/view/edit/{id}","/api/auth/view/update",
                        "/api/auth/role-page","/api/auth/rolepage")
                        .permitAll()
                        .requestMatchers("/Admin/**").hasRole("ADMIN")
                        .requestMatchers("/User/**").hasRole("USER")
                        .requestMatchers("/Manager/**").hasRole("MANAGER")
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())  
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setUserDetailsService(customAdminService);
        provider.setUserDetailsService(customManagerService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
