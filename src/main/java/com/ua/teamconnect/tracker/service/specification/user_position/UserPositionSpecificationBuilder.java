package com.ua.teamconnect.tracker.service.specification.user_position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.service.specification.common.FetchSpecification;
import com.ua.teamconnect.tracker.util.Pair;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Component
public class UserPositionSpecificationBuilder {

    static final String PARAM_SEARCH = "search";
    static final String PARAM_DEPARTMENT = "department";
    static final String PARAM_POSITION = "position";
    static final String PARAM_STACK = "stack";

    /**
     * first - specification for data
     * second - specification for count (without fetch)
     */
    public Pair<Specification<UserPosition>, Specification<UserPosition>> build(
        Map<String, String> params, Set<String> fetchFields
    ) {
        var specification = Specification.<UserPosition>unrestricted();
        specification = SearchUserPositionSpecification.wrap(specification, params.get(PARAM_SEARCH));
        specification = DepartmentIdUserPositionSpecification.wrap(specification, params.get(PARAM_DEPARTMENT));
        specification = PositionIdUserPositionSpecification.wrap(specification, params.get(PARAM_POSITION));
        specification = StackIdUserPositionSpecification.wrap(specification, params.get(PARAM_STACK));
        var now = LocalDate.now();
        specification = NowUserPositionSpecification.wrap(specification, now);
        specification = SingleUserPositionSpecification.wrap(specification);
        var dataSpecification = FetchSpecification.wrap(specification, fetchFields);
        return new Pair<>(dataSpecification, specification);
    }

    public Pair<Specification<UserPosition>, Specification<UserPosition>> build(Map<String, String> params) {
        return build(params, Set.of("user", "position.department"));
    }
}
