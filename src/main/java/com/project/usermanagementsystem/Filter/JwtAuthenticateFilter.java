package com.project.usermanagementsystem.Filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Services.CustomAdminService;
import com.project.usermanagementsystem.Services.CustomManagerService;
import com.project.usermanagementsystem.Services.CustomUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    private CustomUserService customUserService;
    private CustomManagerService customManagerService;
    private CustomAdminService customAdminService;

    public JwtAuthenticateFilter(JwtHelper jwtHelper,CustomUserService customUserService,CustomManagerService customManagerService
    , CustomAdminService customAdminService) {
        this.jwtHelper = jwtHelper;
        this.customUserService = customUserService;
        this.customManagerService=customManagerService;
        this.customManagerService=customManagerService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            String username = jwtHelper.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;
                if(username.startsWith("user")){
                    userDetails= customUserService.loadUserByUsername(username);
                }else if(username.startsWith("admin")){
                    userDetails= customAdminService.loadUserByUsername(username);
                } else{
                    userDetails= customManagerService.loadUserByUsername(username);
                } 
                if (jwtHelper.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
