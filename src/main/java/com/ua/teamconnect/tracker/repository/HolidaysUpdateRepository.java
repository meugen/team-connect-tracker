package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.HolidaysUpdate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidaysUpdateRepository extends CrudRepository<HolidaysUpdate, Integer> {

    boolean existsByYear(int year);
}
