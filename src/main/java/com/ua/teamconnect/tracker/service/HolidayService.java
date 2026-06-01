package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.client.HolidayClient;
import com.ua.teamconnect.tracker.mapper.HolidayMapper;
import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysList;
import com.ua.teamconnect.tracker.model.entity.Holiday;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayClient holidayClient;
    private final HolidayRepository holidayRepository;
    private final TransactionTemplate transactionTemplate;
    private final HolidayMapper holidayMapper;

    @SuppressWarnings("unused")
    public void updateHolidaysInYear(int year) {
        holidayClient.fetchHolidaysInYear(year)
            .map(this::apiToEntity)
            .onErrorComplete()
            .subscribe(holidays -> {
                transactionTemplate.execute(status -> {
                    holidayRepository.saveAll(holidays);
                    return null;
                });
            });
    }

    private Set<Holiday> apiToEntity(HolidaysList holidaysList) {
        return holidaysList.response().holidays()
            .stream()
            .map(holidayApi -> {
                var holiday = new Holiday();
                holiday.setId(holidayApi.genId());
                holiday.setDate(holidayApi.parseLocalDate());
                holiday.setName(holidayApi.name());
                holiday.setDescription(holidayApi.description());
                return holiday;
            })
            .collect(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(Holiday::getId))
            ));
    }

    public List<HolidayDto> findFiveUpcoming() {
        return holidayRepository.findUpcoming(LocalDateTime.now().toLocalDate(), Limit.of(5))
            .stream()
            .map(holidayMapper::entityToDto)
            .toList();
    }
}
