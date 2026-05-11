package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StackRepository extends JpaRepository<Stack, Integer> {
}
