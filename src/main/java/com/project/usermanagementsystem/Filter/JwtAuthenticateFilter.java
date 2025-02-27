package com.project.usermanagementsystem.Filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Services.CustomUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserService customUserService;

    // private static final Logger log =
    // LoggerFactory.getLogger(JwtAuthenticateFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");
                String token = null;
                
                // Check Authorization header first
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
                
                // If no Authorization header, check for token in cookies
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie :cookies) {
                        if ("token".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;  // Exit loop after finding token
                        }
                    }
                }
                
                if (token != null) {
                    String username = jwtHelper.extractUsername(token);
                    jwtHelper.extractRole(token);
                
                    logger.debug("Extracted username from token: " + username);
                    System.out.println("Extracted username from token: " + username);
                
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = customUserService.loadUserByUsername(username);
                
                        if (jwtHelper.validateToken(token, userDetails.getUsername())) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            System.out.println("Invalid ValidateToken");
                        }
                    }
                }
                filterChain.doFilter(request, response);
            }                

}
