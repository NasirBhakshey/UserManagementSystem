package com.project.usermanagementsystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    public Optional<Manager> findByemail(String email);

    public Optional<Manager> findByName(String name);

}
