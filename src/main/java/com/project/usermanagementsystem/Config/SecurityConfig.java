package com.project.usermanagementsystem.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private final JwtAuthenticateFilter jwtAuthFilter;
    @Autowired
    private final CustomUserService userDetailsService;

    public SecurityConfig(JwtAuthenticateFilter jwtAuthFilter,
   CustomUserService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/login-page", "/api/auth/create-user",
                                "/api/auth/dashboard")
                        .permitAll()
                        .requestMatchers("/api/manager/create-manager", "/api/manager/login-manager").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

        // http.csrf(csrf -> csrf.disable())
        // .authorizeHttpRequests(auth -> auth
        // .requestMatchers("/", "/css/**", "/js/**", "/home/login", "/home/**")
        // .authenticated().requestMatchers("/api/auth/login-page")
        // .permitAll()
        // .anyRequest().authenticated())
        // .formLogin(form -> form
        // .loginPage("/home/login")
        // .defaultSuccessUrl("/home/loginpage", true)
        // .permitAll())
        // .logout(logout -> logout
        // .logoutUrl("/logout")
        // .logoutSuccessUrl("/home/login")
        // .permitAll())
        // .sessionManagement(session ->
        // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
