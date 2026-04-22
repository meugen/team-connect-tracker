package com.ua.teamconnect.tracker.repositories;

import com.ua.teamconnect.tracker.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
