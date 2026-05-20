package com.ua.teamconnect.tracker.service.specification.user.position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class CurrentUserPositionSpecification implements Specification<UserPosition> {

    public static Specification<UserPosition> wrap(Specification<UserPosition> spec, LocalDate date) {
        return spec.and(new CurrentUserPositionSpecification(date));
    }

    private final LocalDate now;

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<UserPosition> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var predicates = new Predicate[] {
            criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now), // startDate <= :now
            criteriaBuilder.or( // endDate is null or :now <= endDate
                criteriaBuilder.isNull(root.get("endDate")),
                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), now)
            )
        };
        return criteriaBuilder.and(predicates);
    }
}
