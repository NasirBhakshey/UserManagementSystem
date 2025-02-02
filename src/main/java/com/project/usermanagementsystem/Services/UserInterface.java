package com.project.usermanagementsystem.Services;

import java.util.List;

import com.project.usermanagementsystem.Entities.User;

public interface UserInterface {

    public User InsertUser(User user);

    public boolean updateUser(User user, int id);

    public boolean Loginuser(String email, String pass);

    public List<User> getlldetails();

    public boolean deleteUser(int id);

    public User searchbyID(int id);

}
