package com.project.usermanagementsystem.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
 public class CustomAuthenticationProvider{ //implements AuthenticationProvider{

//     private final UserDetailsService customUserService;
//     private final UserDetailsService customManagerService;
//     private final UserDetailsService customAdminService;
//     private final PasswordEncoder passwordEncoder;

//     public CustomAuthenticationProvider(
//             @Qualifier("customUserService") UserDetailsService customUserService,
//             @Qualifier("customManagerService") UserDetailsService customManagerService,
//             @Qualifier("customAdminService") UserDetailsService customAdminService,
//             PasswordEncoder passwordEncoder) {
//         this.customUserService = customUserService;
//         this.customManagerService = customManagerService;
//         this.customAdminService = customAdminService;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Override
//     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//         String username = authentication.getName();
//         String password = authentication.getCredentials().toString();

//         // Try to load user details from all services
//         UserDetails userDetails = null;
//         try {
//             userDetails = customUserService.loadUserByUsername(username);
//         } catch (UsernameNotFoundException e) {
//             try {
//                 userDetails = customManagerService.loadUserByUsername(username);
//             } catch (UsernameNotFoundException e2) {
//                 try {
//                     userDetails = customAdminService.loadUserByUsername(username);
//                 } catch (UsernameNotFoundException e3) {
//                     throw new UsernameNotFoundException("User not found");
//                 }
//             }
//         }

//         if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
//             return new UsernamePasswordAuthenticationToken(
//                 userDetails, password, userDetails.getAuthorities());
//         }

//         throw new BadCredentialsException("Invalid username or password");
//     }

//     @Override
//     public boolean supports(Class<?> authentication) {
//         // TODO Auto-generated method stub
//         return authentication.equals(UsernamePasswordAuthenticationToken.class);
//     }

}
