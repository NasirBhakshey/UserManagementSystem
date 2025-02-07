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

import com.project.usermanagementsystem.Entities.Manager;
import com.project.usermanagementsystem.Repository.ManagerRepository;

@Service("customManagerService")
public class CustomManagerService implements UserDetailsService{

    @Autowired
    private ManagerRepository managerRepository;

    public CustomManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByemail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found"+email));
        return new org.springframework.security.core.userdetails.User(manager.getEmail(), manager.getPassword(),
                List.of(new SimpleGrantedAuthority(manager.getRoles())));
    }

}
