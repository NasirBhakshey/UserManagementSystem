package com.project.usermanagementsystem.Services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.usermanagementsystem.Repository.UserRepository;

@Service("customUserService")
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        com.project.usermanagementsystem.Entities.User user = userRepository.findByemail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                System.out.println("UserName :"+user.getName()+" Roles : "+user.getRoles());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRoles())));
    }

}
