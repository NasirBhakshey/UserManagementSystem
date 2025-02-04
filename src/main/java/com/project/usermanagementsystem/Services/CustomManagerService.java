package com.project.usermanagementsystem.Services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(manager.getEmail(), manager.getPassword(),
                Collections.emptyList());
    }

}
