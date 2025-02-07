package com.project.usermanagementsystem.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.usermanagementsystem.Entities.Admin;
import com.project.usermanagementsystem.Entities.Manager;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.ManagerRepository;

@Service
public class ManagerImplements implements ManagerInterface{

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomManagerService customManagerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Manager InsertManager(Manager manager) {
        try {
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            return managerRepository.save(manager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateManager(Manager user, int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateManager'");
    }

    @Override
    public String LoginManager(String email, String pass) {
        Optional<Manager> optional=managerRepository.findByemail(email);
        if(optional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        Manager manager=optional.get();
        boolean isMatch=passwordEncoder.matches(pass, manager.getPassword());
        if(!isMatch){
            throw new RuntimeException("Invalid password");
        }
        UserDetails userDetails=customManagerService.loadUserByUsername(email);
        return jwtHelper.generateToken(userDetails, manager.getRoles());
    }

    @Override
    public List<Manager> getlldetails() {
        return managerRepository.findAll();
    }

    @Override
    public boolean deleteManager(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteManager'");
    }

    @Override
    public Manager searchbyID(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchbyID'");
    }

}
