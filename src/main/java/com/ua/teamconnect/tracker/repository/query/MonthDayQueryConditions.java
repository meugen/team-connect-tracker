package com.ua.teamconnect.tracker.repository.query;

public final class MonthDayQueryConditions {

    private MonthDayQueryConditions() {
    }

    public static final String BIRTH_DATE_IN_RANGE = """
       (:startMonth < extract(month from u.birthDate) and extract(month from u.birthDate) < :endMonth)
       or (:startMonth = extract(month from u.birthDate) and extract(month from u.birthDate) < :endMonth and :startDay <= extract(day from u.birthDate))
       or (:startMonth < extract(month from u.birthDate) and extract(month from u.birthDate) = :endMonth and extract(day from u.birthDate) <= :endDay)
       or (:startMonth = extract(month from u.birthDate) and extract(month from u.birthDate) = :endMonth and :startDay <= extract(day from u.birthDate) and extract(day from u.birthDate) <= :endDay)
       """;
}
