package com.project.usermanagementsystem.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.usermanagementsystem.Entities.Admin;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.AdminRepository;

@Service
public class AdminImplements implements AdminInterface {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomAdminService customAdminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin InsertAdmin(Admin admin) {
        try {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            return adminRepository.save(admin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateAdmin(Admin admin, int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAdmin'");
    }

    @Override
    public String LoginAdmin(String email, String pass) {
        Optional<Admin> optional = adminRepository.findByEmail(email);
        if (optional.isEmpty()) {
            throw new RuntimeException("Admin not found");
        }
        Admin admin = optional.get();
        boolean isMatch = passwordEncoder.matches(pass, admin.getPassword());
        if (!isMatch) {
            throw new RuntimeException("Invalid password");
        }
        UserDetails userDetails=customAdminService.loadUserByUsername(email);
        return jwtHelper.generateToken(userDetails, admin.getRoles());
    }

    @Override   
    public List<Admin> getlldetails() {
        return adminRepository.findAll();
    }

    @Override
    public boolean deleteAdmin(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAdmin'");
    }

    @Override
    public Admin searchbyID(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchbyID'");
    }

}
