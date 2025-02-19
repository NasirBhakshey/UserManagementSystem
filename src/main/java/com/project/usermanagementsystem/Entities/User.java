package com.project.usermanagementsystem.Entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.config.Task;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    // @NotBlank(message = "Name is required")
    private String name;

    // @Email(message = "Please provide a valid email address")
    // @NotBlank(message = "Email is required")
    private String email;

    // @NotBlank(message = "Password is required")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(name = "user_roles",
     joinColumns = @JoinColumn(name = "user_id"),
     inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles=new ArrayList<>();

    @OneToMany(mappedBy = "assignedUser", cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH})
    private List<AssignTask> tasks = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    

}
