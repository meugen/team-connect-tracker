package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("select u.role from User u where u.email = :email")
    String findRoleByEmail(String email);

    @Query(nativeQuery = true, value = """
    SELECT u.id userId, u.first_name firstName, u.last_name lastName, u.avatar avatarUrl, uh.hire_date hireDate
    FROM users u JOIN user_hire_date uh on (u.id = uh.user_id)
    WHERE (:startMonth < EXTRACT(MONTH FROM uh.hire_date) AND EXTRACT(MONTH FROM uh.hire_date) < :endMonth)
       OR (:startMonth = EXTRACT(MONTH FROM uh.hire_date) AND EXTRACT(MONTH FROM uh.hire_date) < :endMonth AND :startDay <= EXTRACT(DAY FROM uh.hire_date))
       OR (:startMonth < EXTRACT(MONTH FROM uh.hire_date) AND EXTRACT(MONTH FROM uh.hire_date) = :endMonth AND EXTRACT(DAY FROM uh.hire_date) <= :endDay)
       OR (:startMonth = EXTRACT(MONTH FROM uh.hire_date) AND EXTRACT(MONTH FROM uh.hire_date) = :endMonth AND :startDay <= EXTRACT(DAY FROM uh.hire_date) AND EXTRACT(DAY FROM uh.hire_date) <= :endDay)
    """)
    List<Anniversary> findAnniversaries(int startMonth, int startDay, int endMonth, int endDay);
}
