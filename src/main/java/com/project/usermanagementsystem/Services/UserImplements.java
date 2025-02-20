package com.project.usermanagementsystem.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.usermanagementsystem.Entities.AssignTask;
import com.project.usermanagementsystem.Entities.Role;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Helper.JwtHelper;
import com.project.usermanagementsystem.Repository.RoleRepository;
import com.project.usermanagementsystem.Repository.TaskRepository;
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

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public User InsertUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Role role = roleRepository.findByName("USER").get();
            user.getRoles().add(role);
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateUser(User user, List<Integer> roleIds, int id) {
        System.out.println("Id user :" + id);
        User user2 = searchbyID(id);
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (user2 != null) {
            user2.setName(user.getName());
            user2.setEmail(user.getEmail());
            user2.setPassword(passwordEncoder.encode(user.getPassword()));
            user2.setRoles(roles);
            userRepository.save(user2);
            return true;
        } else {
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
        System.out.println("IMM");
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

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public Role InsertRole(Role role) {
        try {
            return roleRepository.save(role);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getUserrole(int id) {
        return userRepository.hasUserRole(id);
    }

    @Override
    public AssignTask InsertTask(AssignTask assignTask, Integer userId) {

        try {
            User user = searchbyID(userId);
            assignTask.setAssignedUser(user);
            return taskRepository.save(assignTask);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<AssignTask> getallTask() {
        return taskRepository.findAll();
    }

    @Override
    public List<User> getallUser() {
        return userRepository.findAllUsersWithUserRole();
    }

    @Override
    public AssignTask searchbyTaskID(int id) {
        Optional<AssignTask> optional = taskRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteTask(int id) {
        AssignTask task = searchbyTaskID(id);
        if (task != null) {
            taskRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateTask(AssignTask assignTask, int id, Integer userId) {
        AssignTask task = searchbyTaskID(id);
        User user = searchbyID(userId);
        if (task != null) {
            task.setTitle(assignTask.getTitle());
            task.setTaskDescription(assignTask.getTaskDescription());
            task.setAssignedUser(user);
            taskRepository.save(task);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<AssignTask> getTaskByUser(User user) {
        return taskRepository.findByAssignedUser(user);
    }

}
