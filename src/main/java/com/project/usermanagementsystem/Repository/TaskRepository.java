package com.project.usermanagementsystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.AssignTask;

public interface TaskRepository extends JpaRepository<AssignTask,Integer>{

}
