package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.UserDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.ua.teamconnect.tracker.repository.query.MonthDayQueryConditions.BIRTH_DATE_IN_RANGE;
import static com.ua.teamconnect.tracker.repository.query.MonthDayQueryConditions.HIRE_DATE_IN_RANGE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("select u.role from User u where u.email = :email")
    String findRoleByEmail(String email);

    @Query("""
    select up.id.userId userId, up.user.firstName firstName, up.user.lastName lastName, up.user.avatar avatarUrl, up, up.position position, uh.hireDate hireDate
    from UserPosition up join fetch up.position p join fetch p.department
    join (select up.id.userId userId, max(up.startDate) hireDate from UserPosition up group by up.id.userId) uh on (uh.userId=up.id.userId)
    where """ + HIRE_DATE_IN_RANGE)
    List<UserDate> findAnniversaries(int startMonth, int startDay, int endMonth, int endDay);

    @Query("""
    select up.id.userId userId, up.user.firstName firstName, up.user.lastName lastName, up.user.avatar avatarUrl, up, up.position position, uh.hireDate hireDate
    from UserPosition up join fetch up.position p join fetch p.department
    join (select up.id.userId userId, max(up.startDate) hireDate from UserPosition up group by up.id.userId) uh on (uh.userId=up.id.userId)
    where :startDate <= uh.hireDate and uh.hireDate <= :endDate
    """)
    List<UserDate> findByHireDate(LocalDate startDate, LocalDate endDate);
    
    @Query("""
    select u
    from User u
    where (""" + BIRTH_DATE_IN_RANGE + """
    )
    order by u.lastName, u.firstName
    """)
    List<User> findUsersWithBirthdaysBetween(int startMonth, int startDay, int endMonth, int endDay);
}
