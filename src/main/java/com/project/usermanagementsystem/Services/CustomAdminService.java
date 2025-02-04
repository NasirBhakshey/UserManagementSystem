package com.project.usermanagementsystem.Services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.usermanagementsystem.Entities.Admin;
import com.project.usermanagementsystem.Repository.AdminRepository;

@Service("customAdminService")
public class CustomAdminService implements UserDetailsService{

    @Autowired
    private AdminRepository adminRepository;

    public CustomAdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       
        Admin admin = adminRepository.findByemail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getPassword(),
                Collections.emptyList());
    }

}
