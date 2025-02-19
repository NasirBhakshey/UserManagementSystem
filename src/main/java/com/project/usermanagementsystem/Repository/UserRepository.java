package com.project.usermanagementsystem.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.usermanagementsystem.Entities.User;

public interface UserRepository extends JpaRepository<User,Integer>{

   public Optional<User> findByemail(String email);

   public Optional<User> findByName(String name);

   @Query(value = "SELECT r.name FROM user u " +
   "JOIN user_roles ur ON u.id = ur.user_id " +
   "JOIN role r ON ur.role_id = r.id " +
   "WHERE u.id = :userId LIMIT 1", 
nativeQuery = true)
   public String hasUserRole(@Param("userId") Integer userId);


   @Query(value = "SELECT u.id, u.name, u.email, r.name AS role_name " +
                  "FROM user u " +
                  "JOIN user_roles ur ON u.id = ur.user_id " +
                  "JOIN role r ON ur.role_id = r.id " +
                  "WHERE u.id = :userId", nativeQuery = true)
   List<Object[]> findUserWithRoles(@Param("userId") Integer userId);

}
