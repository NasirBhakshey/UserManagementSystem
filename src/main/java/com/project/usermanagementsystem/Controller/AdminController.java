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

import com.project.usermanagementsystem.Entities.Admin;
import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.Manager;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.AdminRepository;
import com.project.usermanagementsystem.Services.AdminImplements;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminImplements adminImplements;

    public AdminController(JwtHelper jwtHelper, AdminRepository adminRepository, AdminImplements adminImplements) {
        this.jwtHelper = jwtHelper;
        this.adminRepository = adminRepository;
        this.adminImplements = adminImplements;
    }

    @PostMapping("/create-admin")
    public Admin insertAdmin(@RequestBody Admin admin) {
        return adminImplements.InsertAdmin(admin);
    }

    @GetMapping("/loginadmin")
    public ModelAndView loginPage() {
        return new ModelAndView("admin_login", "admin", new Admin());
    }

    @PostMapping(value = "/login-admin")
    public ResponseEntity<?> loginpage(@RequestBody(required = false) Admin jsonAdmin, HttpSession session,
            @ModelAttribute("formAdmin") Admin formAdmin) {

        Admin admin;
        if (jsonAdmin != null) {
            // Handle JSON request
            admin = jsonAdmin;
        } else {
            // Handle form data request
            admin = formAdmin;
        }
        System.out.println("Outer Success...");
        if (admin == null || admin.getEmail() == null || admin.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and password are required");
        }
        try {
            System.out.println("Inner Success");
            String admin2 = adminImplements.LoginAdmin(admin.getEmail(), admin.getPassword());

            Admin admin3 = adminRepository.findByEmail(admin.getEmail()).get();

            session.setAttribute("admin", admin3);

            JwtToken jwtToken = JwtToken.builder().name(admin3.getName()).JwtToken(admin2).build();
            System.out.println("Success...");
            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/admindashboard")
    public ModelAndView dashboard(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return new ModelAndView("redirect:/api/manager/loginmanager");
        }
        ModelAndView modelAndView = new ModelAndView("manager_dashboard");
        modelAndView.addObject("admin", admin);
        return modelAndView;
    }

    @GetMapping("/view-admin")
    public List<Admin> getalldetails() {
        return adminImplements.getlldetails();
    }
}
