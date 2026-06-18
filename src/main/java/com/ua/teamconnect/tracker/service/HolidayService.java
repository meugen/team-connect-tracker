package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.client.HolidayClient;
import com.ua.teamconnect.tracker.mapper.HolidayMapper;
import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.model.dto.HolidayRequestDto;
import com.ua.teamconnect.tracker.model.dto.api.calendarific.HolidaysList;
import com.ua.teamconnect.tracker.model.entity.Holiday;
import com.ua.teamconnect.tracker.model.exception.DuplicateHolidayException;
import com.ua.teamconnect.tracker.model.exception.HolidayNotFoundException;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final Logger logger = LoggerFactory.getLogger(HolidayService.class);

    private final HolidayClient holidayClient;
    private final HolidayRepository holidayRepository;
    private final TransactionTemplate transactionTemplate;
    private final HolidayMapper holidayMapper;

    public Mono<Set<Holiday>> updateHolidaysInYear(int year) {
        return holidayClient.fetchHolidaysInYear(year)
            .map(this::apiToEntity)
            .doOnError(throwable -> {
                logger.error("Failed to fetch holidays in year {}", year, throwable);
            })
            .doOnSuccess(holidays -> {
                transactionTemplate.execute(status -> {
                    var ids = holidayRepository.findAllIdsInYear(year);
                    var filteredHolidays = holidays.stream()
                        .filter(holiday -> !ids.contains(holiday.getId()))
                        .toList();
                    holidayRepository.saveAll(filteredHolidays);
                    logger.info(
                        "Holiday sync completed for year {}. Received: {}, saved: {}",
                        year, holidays.size(), filteredHolidays.size()
                    );
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
                holiday.setIsDayOff(Boolean.TRUE);
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

    public HolidayDto create(HolidayRequestDto dto) {
        var holiday = new Holiday();
        holiday.setId(UUID.randomUUID().toString());
        if (holidayRepository.existsByNameAndDate(dto.name(), dto.date())) {
            throw new DuplicateHolidayException();
        }
        holidayMapper.dtoToEntity(dto, holiday);
        var savedHoliday = holidayRepository.save(holiday);
        return holidayMapper.entityToDto(savedHoliday);
    }

    public HolidayDto update(String holidayId, HolidayRequestDto dto) {
        var holiday = holidayRepository.findById(holidayId).orElseThrow(
            () -> new HolidayNotFoundException(holidayId)
        );
        if (holidayRepository.existsByNameAndDateAndIdNot(dto.name(), dto.date(), holidayId)) {
            throw new DuplicateHolidayException();
        }
        holidayMapper.dtoToEntity(dto, holiday);
        var savedHoliday = holidayRepository.save(holiday);
        return holidayMapper.entityToDto(savedHoliday);
    }
}
