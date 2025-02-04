package com.project.usermanagementsystem.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.Manager;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.ManagerRepository;
import com.project.usermanagementsystem.Services.ManagerImplements;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerImplements managerImplements;

    public ManagerController(JwtHelper jwtHelper, ManagerRepository managerRepository,
            ManagerImplements managerImplements) {
        this.jwtHelper = jwtHelper;
        this.managerRepository = managerRepository;
        this.managerImplements = managerImplements;
    }

    @PostMapping("/create-manager")
    public Manager insertUser(@RequestBody Manager manager) {
        return managerImplements.InsertManager(manager);
    }

    @GetMapping("/loginmanager")
    public ModelAndView loginPage() {
        return new ModelAndView("login", "manager", new Manager());
    }

    @PostMapping(value = "/login-manager")
    public ResponseEntity<?> loginpage(@RequestBody(required = false) Manager jsonManager, HttpSession session,
            @ModelAttribute(binding = false) Manager manager1) {

        Manager manager;
        if (jsonManager != null) {
            // Handle JSON request
            manager = jsonManager;
        } else {
            // Handle form data request
            manager = manager1;
        }

        if (manager == null || manager.getEmail() == null || manager.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password are required");
        }
        try {
            String manager2 = managerImplements.LoginManager(manager.getEmail(), manager.getPassword());

            Manager manager3 = managerRepository.findByemail(manager.getEmail()).get();

            session.setAttribute("manager", manager3);

            // ModelAndView modelAndView = new ModelAndView("redirect:/api/auth/dashboard");
            JwtToken jwtToken = JwtToken.builder().name(manager3.getName()).JwtToken(manager2).build();
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
        Manager manager = (Manager) session.getAttribute("user");
        if (manager == null) {
            return new ModelAndView("redirect:/api/auth/login");
        }
        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", manager);
        return modelAndView;
    }

    @GetMapping("/view-manager")
    public List<Manager> getalldetails() {
        return managerImplements.getlldetails();
    }

}
