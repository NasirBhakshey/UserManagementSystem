package com.project.usermanagementsystem.Services;

import java.util.List;

import com.project.usermanagementsystem.Entities.Admin;

public interface AdminInterface {

    public Admin InsertAdmin(Admin admin);

    public boolean updateAdmin(Admin admin, int id);

    public String LoginAdmin(String email, String pass);

    public List<Admin> getlldetails();

    public boolean deleteAdmin(int id);

    public Admin searchbyID(int id);
}
