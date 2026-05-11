package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("""
    select up.id.userId userId, up.user.firstName firstName, up.user.lastName lastName,
         up.user.avatar avatarUrl, min(up.startDate) hireDate from UserPosition up
         group by up.id.userId, up.user.firstName, up.user.lastName, up.user.avatar
    """)
    List<Anniversary> findAllAnniversaries();

    default List<Anniversary> findAnniversaries(LocalDate start, LocalDate end) {
        return findAllAnniversaries().stream()
            .filter(anniversary -> {
                var startDate = start.withYear(anniversary.getHireDate().getYear());
                var endDate = end.withYear(anniversary.getHireDate().getYear());
                return anniversary.getHireDate().isAfter(startDate)
                    && anniversary.getHireDate().isBefore(endDate);
            })
            .toList();
    }
}
