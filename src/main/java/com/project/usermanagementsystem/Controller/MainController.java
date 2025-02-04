package com.project.usermanagementsystem.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.AdminRepository;
import com.project.usermanagementsystem.Repository.ManagerRepository;
import com.project.usermanagementsystem.Repository.UserRepository;
import com.project.usermanagementsystem.Services.AdminImplements;
import com.project.usermanagementsystem.Services.ManagerImplements;
import com.project.usermanagementsystem.Services.UserImplements;
// import com.project.usermanagementsystem.model.JwtRequest;
// import com.project.usermanagementsystem.model.JwtResponse;

import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login", "user", new User());
    }

    @PostMapping(value = "/login-page")
    public ResponseEntity<?> loginpage(@RequestBody(required = false) User jsonUser, HttpSession session,
            @RequestParam(required = false) String email, @RequestParam(required = false) String password) {

        User user;
        if (jsonUser != null) {
            // Handle JSON request
            user = jsonUser;
        } else {
            // Handle form data request
            user = new User();
            user.setEmail(email);
            user.setPassword(password);
        }

        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password are required");
        }
        try {
            String user1 = userImplements.Loginuser(user.getEmail(), user.getPassword());

            User user2 = userRepository.findByemail(user.getEmail()).get();

            session.setAttribute("user", user2);

            // ModelAndView modelAndView = new ModelAndView("redirect:/api/auth/dashboard");
            JwtToken jwtToken = JwtToken.builder().name(user2.getName()).JwtToken(user1).build();
            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
            // return modelAndView;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            // ModelAndView modelAndView = new ModelAndView("login");
            // modelAndView.addObject("error", "Invalid credentials");
            // return modelAndView;
        }
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/api/auth/login");
        }
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/view-users")
    public List<User> getalldetails() {
        return userImplements.getlldetails();
    }

}
