package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    List<Position> findByDepartmentId(Integer departmentId);
}
