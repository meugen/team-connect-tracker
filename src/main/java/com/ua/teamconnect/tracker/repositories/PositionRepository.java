package com.ua.teamconnect.tracker.repositories;

import com.ua.teamconnect.tracker.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByDepartmentId(Long departmentId);
}
