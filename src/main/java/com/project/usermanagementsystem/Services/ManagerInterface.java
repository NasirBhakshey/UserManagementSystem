package com.project.usermanagementsystem.Services;

import java.util.List;

import com.project.usermanagementsystem.Entities.Manager;

public interface ManagerInterface {

    public Manager InsertManager(Manager manager);

    public boolean updateManager(Manager user, int id);

    public String LoginManager(String email, String pass);

    public List<Manager> getlldetails();

    public boolean deleteManager(int id);

    public Manager searchbyID(int id);

}
