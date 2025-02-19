package com.project.usermanagementsystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.Role;

public interface RoleRepository extends JpaRepository<Role,Integer>{

public Optional<Role> findByName(String name);
}
