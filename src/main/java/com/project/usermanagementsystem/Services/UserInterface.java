package com.project.usermanagementsystem.Services;

import java.util.List;
import java.util.Set;

import com.project.usermanagementsystem.Entities.AssignTask;
import com.project.usermanagementsystem.Entities.Role;
import com.project.usermanagementsystem.Entities.User;

public interface UserInterface {

    public User InsertUser(User user);

    public boolean updateUser(User user, List<Integer> roleIds,int id);

    public String Loginuser(String name, String pass);

    public List<User> getlldetails();

    public boolean deleteUser(int id);

    public User searchbyID(int id);

    public List<Role> getAllRole();

    public Role InsertRole(Role role);

    public String getUserrole(int id);

    public AssignTask InsertTask(AssignTask assignTask,Integer userId);

    public List<AssignTask> getallTask();

}
