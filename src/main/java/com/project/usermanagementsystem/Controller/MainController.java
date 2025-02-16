package com.project.usermanagementsystem.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.UserRepository;
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

    @GetMapping("/reg-page")
    public ModelAndView registerPage() {
        return new ModelAndView("register_user", "user", new User());
    }

    @PostMapping(value = "/create-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserJson(@RequestBody User user) {
        ResponseEntity<?> response = processUserRegistration(user);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("User registered successfully. Please log in.");
        }
        return response;
    }

    @PostMapping(value = "/create-user")
    public ModelAndView registerUserForm(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        ResponseEntity<?> response = processUserRegistration(user);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successmsg", "User registered successfully. Please log in.");
            return new ModelAndView("redirect:/api/auth/loginpage");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", response.getBody());
            return new ModelAndView("redirect:/api/auth/reg-page");
        }
    }

    private ResponseEntity<?> processUserRegistration(User user) {
        Optional<User> existingUser = userRepository.findByemail(user.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This Email-ID is already registered...");
        }
        try {
            User savedUser = userImplements.InsertUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/loginpage")
    public ModelAndView loginPage() {
        return new ModelAndView("login", "user", new User());
    }

    @PostMapping(value = "/login-page")
    public ResponseEntity<?> loginpage(@RequestBody(required = false) User jsonUser, HttpSession session,
            @ModelAttribute("user") User formUser) {

        User user;
        if (jsonUser != null) {
            user = jsonUser;
        } else {
            user = formUser;
        }

        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password are required");
        }
        try {
            String user1 = userImplements.Loginuser(user.getEmail(), user.getPassword());

            User user2 = userRepository.findByemail(user.getEmail()).get();

            session.setAttribute("user", user2);
            JwtToken jwtToken = JwtToken.builder().name(user2.getName()).JwtToken(user1).build();
            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        String getrole = user.getRoles().toUpperCase();
        switch (getrole) {
            case "ADMIN":
                ModelAndView modelAndView = new ModelAndView("Admin_Dashboard");
                modelAndView.addObject("admin", user);
                return modelAndView;
            case "MANAGER":
                ModelAndView modelAndView1 = new ModelAndView("manager_dashboard");
                modelAndView1.addObject("manager", user);
                return modelAndView1;
            case "USER":
                ModelAndView modelAndView2 = new ModelAndView("dashboard");
                modelAndView2.addObject("user", user);
                return modelAndView2;
            default:
                return new ModelAndView("redirect:/api/auth/loginpage");
        }
    }

    @GetMapping("/view-users")
    public ModelAndView getalldetails(Model model) {
        List<User> listuser = userImplements.getlldetails();
        ModelAndView modelAndView = new ModelAndView("view_user");
        modelAndView.addObject("listuser", listuser);
        return modelAndView;
    }

    @GetMapping("/view/edit/{id}")
    public ModelAndView Editpage(@PathVariable("id") int id, Model model) {
        User user = userImplements.searchbyID(id);
        ModelAndView modelAndView = new ModelAndView("update_user");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/view/update")
    public ModelAndView updatepage(@ModelAttribute("user") User user,
            RedirectAttributes redirectAttributes) {
        boolean update = userImplements.updateUser(user, user.getId());
        if (update) {
            redirectAttributes.addFlashAttribute("Successmsg", "User Update SuccessFull....");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", "Error While Updating User...");
        }
        return new ModelAndView("redirect:/api/auth/view-users");

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        System.out.println("Delete");
        boolean deleted = userImplements.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @GetMapping("/view/delete/{id}")
    public ModelAndView confirmDelete(@PathVariable int id, Model model) {
        User user = userImplements.searchbyID(id);
        ModelAndView modelAndView = new ModelAndView("delete_confirm");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/view/delete/{id}")
    public ModelAndView deleteUserWithForm(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean deleted = userImplements.deleteUser(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("Successmsg", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", "User could not be found!");
        }
        return new ModelAndView("redirect:/api/auth/view-users");
    }

}
