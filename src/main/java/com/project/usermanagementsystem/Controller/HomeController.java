package com.project.usermanagementsystem.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Services.UserImplements;

@RestController
@RequestMapping("/home")
public class HomeController {

    private UserImplements userImplements;

    

     public HomeController(UserImplements userImplements) {
        this.userImplements = userImplements;
    }



    @GetMapping("/view-users")
    public List<User> getalldetails() {
        return userImplements.getlldetails();
    }


}
