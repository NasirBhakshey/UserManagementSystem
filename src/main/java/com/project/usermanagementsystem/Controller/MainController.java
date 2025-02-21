package com.project.usermanagementsystem.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.usermanagementsystem.Entities.AssignTask;
import com.project.usermanagementsystem.Entities.JwtToken;
import com.project.usermanagementsystem.Entities.Role;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Repository.UserRepository;
import com.project.usermanagementsystem.Services.UserImplements;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class MainController {

    @Autowired
    private UserImplements userImplements;
    @Autowired
    private UserRepository userRepository;

    public MainController(UserImplements userImplements, UserRepository userRepository) {
        this.userImplements = userImplements;
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password...");
        }
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession session) {
        System.out.println("User Inner Role :-");
        User user = (User) session.getAttribute("user");
        String role = userImplements.getUserrole(user.getId());
        System.out.println("User Role :-" + role + "User ID:-" + user.getId());

        if ("ADMIN".equalsIgnoreCase(role)) {
            ModelAndView modelAndView = new ModelAndView("Admin_Dashboard");
            modelAndView.addObject("admin", user);
            return modelAndView;
        } else if ("MANAGER".equalsIgnoreCase(role)) {
            ModelAndView modelAndView1 = new ModelAndView("manager_dashboard");
            modelAndView1.addObject("manager", user);
            return modelAndView1;
        } else if ("USER".equalsIgnoreCase(role)) {
            User user1 = userImplements.searchbyID(user.getId());
            ModelAndView modelAndView2 = new ModelAndView("dashboard");
            modelAndView2.addObject("user", user1);
            modelAndView2.addObject("user", user);
            return modelAndView2;
        } else {
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
    public ModelAndView Editpage(@PathVariable("id") int id) {
        User user = userImplements.searchbyID(id);
        List<Role> rolelist = userImplements.getAllRole();
        ModelAndView modelAndView = new ModelAndView("update_user");
        modelAndView.addObject("user", user);
        modelAndView.addObject("rolelist", rolelist);
        return modelAndView;
    }

    @PostMapping("/view/update")
    public ModelAndView updatepage(@ModelAttribute("user") User user,
            @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
            RedirectAttributes redirectAttributes, Model model) {
        boolean update = userImplements.updateUser(user, roleIds, user.getId());
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
        ModelAndView modelAndView = new ModelAndView("confirm_delete_user");
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

    @GetMapping("/role-page")
    public ModelAndView rolePage() {
        return new ModelAndView("role_page", "role", new Role());
    }

    @PostMapping(value = "/rolepage")
    public ModelAndView AssignRoleForm(@ModelAttribute("role") Role role, RedirectAttributes redirectAttributes) {
        ResponseEntity<?> response = processAssignRegistration(role);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successmsg", "User registered successfully. Please log in.");
            return new ModelAndView("redirect:/api/auth/dashboard");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", response.getBody());
            return new ModelAndView("redirect:/api/auth/role-page");
        }
    }

    @PostMapping(value = "/rolepage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> AssignRoleJson(@RequestBody Role role) {
        ResponseEntity<?> response = processAssignRegistration(role);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("User registered successfully. Please log in.");
        }
        return response;
    }

    private ResponseEntity<?> processAssignRegistration(Role role) {
        try {
            Role savedrole = userImplements.InsertRole(role);
            return ResponseEntity.status(HttpStatus.OK).body(savedrole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/task-page")
    public ModelAndView taskPage() {
        List<User> listuser = userImplements.getallUser();
        ModelAndView modelAndView = new ModelAndView("task_page");
        modelAndView.addObject("listuser", listuser);
        return modelAndView;
    }

    @PostMapping(value = "/taskpage")
    public ModelAndView AssignTaskForm(@ModelAttribute("assignTask") AssignTask assignTask,
            @RequestParam(value = "userId", required = false) Integer userId, RedirectAttributes redirectAttributes) {
        ResponseEntity<?> response = processAssignTask(assignTask, userId);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successmsg", "Assign Task to User successfully...");
            return new ModelAndView("redirect:/api/auth/dashboard");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", response.getBody());
            return new ModelAndView("redirect:/api/auth/task-page");
        }
    }

    @PostMapping(value = "/taskpage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> AssignTaskJson(@RequestBody AssignTask assignTask,
            @RequestParam(value = "userId", required = false) Integer userId) {
        ResponseEntity<?> response = processAssignTask(assignTask, userId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("Assign Task to User successfully...");
        }
        return response;
    }

    private ResponseEntity<?> processAssignTask(AssignTask assignTask, Integer userId) {
        try {
            AssignTask savedTask = userImplements.InsertTask(assignTask, userId);
            return ResponseEntity.status(HttpStatus.OK).body(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/view-task")
    public ModelAndView getallTask(Model model) {
        List<AssignTask> listTask = userImplements.getallTask();
        ModelAndView modelAndView = new ModelAndView("view_task");
        modelAndView.addObject("listTask", listTask);
        return modelAndView;
    }

    @GetMapping("/task/edit/{id}")
    public ModelAndView EditTask(@PathVariable("id") int id) {
        AssignTask assignTask = userImplements.searchbyTaskID(id);
        List<User> userlist = userImplements.getallUser();
        ModelAndView modelAndView = new ModelAndView("update_task");
        modelAndView.addObject("assignTask", assignTask);
        modelAndView.addObject("userlist", userlist);
        return modelAndView;
    }

    @PostMapping("/task/update")
    public ModelAndView updatetask(@ModelAttribute("assignTask") AssignTask assignTask,
            @RequestParam(value = "assignedUser.id", required = false) Integer userId,
            RedirectAttributes redirectAttributes, Model model) {
        System.out.println("User ID:- " + userId);
        boolean update = userImplements.updateTask(assignTask, assignTask.getId(), userId);
        if (update) {
            redirectAttributes.addFlashAttribute("Successmsg", "Task Update SuccessFull....");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", "Error While Updating Task...");
        }
        return new ModelAndView("redirect:/api/auth/view-task");

    }

    @GetMapping("/task/delete/{id}")
    public ModelAndView confirmtaskDelete(@PathVariable int id, Model model) {
        AssignTask assignTask = userImplements.searchbyTaskID(id);
        ModelAndView modelAndView = new ModelAndView("delete_confirm");
        modelAndView.addObject("user", assignTask);
        return modelAndView;
    }

    @PostMapping("/task/delete/{id}")
    public ModelAndView deletetaskWithForm(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean deleted = userImplements.deleteTask(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("Successmsg", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errormsg", "User could not be found!");
        }
        return new ModelAndView("redirect:/api/auth/view-task");
    }

    @GetMapping("/view-usertask")
    public ModelAndView getTaskByUser(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<AssignTask> listTask = userImplements.getTaskByUser(user);
        ModelAndView modelAndView = new ModelAndView("view_usertask");
        modelAndView.addObject("listTask", listTask);
        return modelAndView;
    }

    @GetMapping("/json/logout")
    public ResponseEntity<String> logoutJson(HttpServletRequest request, HttpServletResponse response) {
        // Remove JWT token (if stored in cookies)
        response.setHeader("Authorization", ""); 
        request.getSession().invalidate();// Clear token
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/logout")
    public ModelAndView logoutform(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Authorization", ""); 
        request.getSession().invalidate();// Clear token
        return new ModelAndView("redirect:/api/auth/loginpage"); // Redirect to login page
    }

}
