package com.ua.teamconnect.tracker.repositories;

import com.ua.teamconnect.tracker.model.Stack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StackRepository extends JpaRepository<Stack, Long> {
}
