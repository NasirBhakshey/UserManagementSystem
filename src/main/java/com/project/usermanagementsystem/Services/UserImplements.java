package com.project.usermanagementsystem.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.UserRepository;

@Service
public class UserImplements implements UserInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public User InsertUser(User user) {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles("User");
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateUser(User user, int id) {
        User user2=searchbyID(id);
        if(user2!=null){
            user2.setName(user.getName());
            user2.setEmail(user.getEmail());
            user2.setPassword(passwordEncoder.encode(user.getPassword()));
            user2.setRoles(user.getRoles());
            userRepository.save(user2);
            return true;
        }else{
            return false;
        }

    }

    @Override
    public String Loginuser(String email, String pass) {
        Optional<User> optional = userRepository.findByemail(email);
        if (optional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optional.get();
        boolean isMatch = passwordEncoder.matches(pass, user.getPassword());
        if (!isMatch) {
            throw new RuntimeException("Invalid password");
        }
        UserDetails userDetails = customUserService.loadUserByUsername(email);
        return jwtHelper.generateToken(userDetails, user.getRoles());

    }

    @Override
    public List<User> getlldetails() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User searchbyID(int id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

}
