package com.project.usermanagementsystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.User;

public interface UserRepository extends JpaRepository<User,Integer>{

   public Optional<User> findByemail(String email);

   public Optional<User> findByName(String name);

}
