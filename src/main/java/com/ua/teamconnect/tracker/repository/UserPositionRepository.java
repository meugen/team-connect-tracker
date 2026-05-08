package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.entity.id.UserPositionId;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPositionRepository extends CrudRepository<UserPosition, UserPositionId> {

    @Query("""
    select up from UserPosition up join fetch up.position p join fetch p.department
        where up.id.userId=:userId and up.startDate <= :now and (up.endDate is null or :now < up.endDate)
    """)
    List<UserPosition> findByUserIdAndNow(Long userId, LocalDate now);

    @Query("select min(up.startDate) hire_date from UserPosition up where up.id.userId=:userId")
    Optional<LocalDate> findHireDateByUserId(Long userId);

    @Query("""
    select up.id.userId userId, up.user.firstName firstName, up.user.lastName lastName,
        up.user.avatar avatarUrl, min(up.startDate) hireDate from UserPosition up
        group by userId, firstName, lastName, avatarUrl
        having (:monthStart < month(min(up.startDate)) and month(min(up.startDate)) < :monthEnd)
            or (:monthStart = month(min(up.startDate)) and month(min(up.startDate)) < :monthEnd and :dayStart <= day(min(up.startDate)))
            or (:monthStart < month(min(up.startDate)) and month(min(up.startDate)) = :monthEnd and day(min(up.startDate)) <= :dayEnd)
            or (:monthStart = month(min(up.startDate)) and month(min(up.startDate)) = :monthEnd and :dayStart <= day(min(up.startDate)) and day(min(up.startDate)) <= :dayEnd)
    """)
    List<Anniversary> findAnniversaries(int monthStart, int dayStart, int monthEnd, int dayEnd);
}
