package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Holiday;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends CrudRepository<Holiday, Integer> {

    @Query("""
    select h from Holiday h where :now <= h.date order by h.date asc
    """)
    List<Holiday> findUpcoming(LocalDate now, Limit limit);
}
