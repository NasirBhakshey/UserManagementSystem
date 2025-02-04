package com.project.usermanagementsystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer>{

    public Optional<Admin> findByemail(String email);

}
