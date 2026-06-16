package com.ua.teamconnect.tracker.repository.specification.holiday;

import com.ua.teamconnect.tracker.model.entity.Holiday;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@RequiredArgsConstructor
public class DuplicateHolidaySpecification implements Specification<Holiday> {

    private final String name;
    private final LocalDate date;
    private final String id;

    public DuplicateHolidaySpecification(String name, LocalDate date) {
        this(name, date, null);
    }

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<Holiday> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var predicate = criteriaBuilder.equal(root.get("name"), name);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("date"), date));
        if (id != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get("id"), id));
        }
        return predicate;
    }
}
