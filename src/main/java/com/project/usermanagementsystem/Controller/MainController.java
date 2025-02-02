package com.project.usermanagementsystem.Controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.UserRepository;
import com.project.usermanagementsystem.Services.UserImplements;
// import com.project.usermanagementsystem.model.JwtRequest;
// import com.project.usermanagementsystem.model.JwtResponse;

@RestController
@RequestMapping("/api/auth")
public class MainController {

    @Autowired
    private UserImplements userImplements;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserRepository userRepository;

    public MainController(UserImplements userImplements, JwtHelper jwtHelper, UserRepository userRepository) {
        this.userImplements = userImplements;
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-user")
    public User insertUser(@RequestBody User user) {
        return userImplements.InsertUser(user);
    }

    @PostMapping("/login-page")
    public ResponseEntity<JwtToken> loginpage(@RequestBody User user) {

        boolean user1 = userImplements.Loginuser(user.getEmail(), user.getPassword());

        User user2 = userRepository.findByemail(user.getEmail()).get();

        if (user1) {
            String token = jwtHelper.generateToken(user.getEmail(), user2.getPassword());
            JwtToken jwtToken = JwtToken.builder().name(user2.getName()).JwtToken(token).build();
            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/view-users")
    public List<User> getalldetails() {
        return userImplements.getlldetails();
    }

}
