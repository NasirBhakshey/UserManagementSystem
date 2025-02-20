package com.project.usermanagementsystem.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.usermanagementsystem.Entities.AssignTask;
import com.project.usermanagementsystem.Entities.User;

public interface TaskRepository extends JpaRepository<AssignTask,Integer>{

    List<AssignTask> findByAssignedUser(User assignedUser);

}
