package com.ua.teamconnect.tracker.repository.specification.common;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor(access = PRIVATE)
public class FetchSpecification<T> implements Specification<T> {

    public static <T> Specification<T> wrap(Specification<T> spec, Set<String> fetchFields) {
        if (isEmpty(fetchFields)) return spec;
        return spec.and(new FetchSpecification<>(fetchFields));
    }

    private final Set<String> fetchFields;

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<T> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        fetchFields.forEach(field -> {
            FetchParent<?, ?> fetcher = root;
            for (var item : field.split("\\.")) {
                fetcher = fetcher.fetch(item, JoinType.INNER);
            }
        });
        return null;
    }
}
