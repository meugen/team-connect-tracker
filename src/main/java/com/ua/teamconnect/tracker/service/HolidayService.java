package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.client.HolidayClient;
import com.ua.teamconnect.tracker.mapper.HolidayMapper;
import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.model.entity.HolidaysUpdate;
import com.ua.teamconnect.tracker.repository.HolidayRepository;
import com.ua.teamconnect.tracker.repository.HolidaysUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayClient holidayClient;
    private final HolidayRepository holidayRepository;
    private final HolidaysUpdateRepository holidaysUpdateRepository;
    private final TransactionTemplate transactionTemplate;
    private final HolidayMapper holidayMapper;

    public void updateHolidaysInYear(int year) {
        if (holidaysUpdateRepository.existsByYear(year)) return;
        holidayClient.fetchHolidaysInYear(year).onErrorComplete() .subscribe(holidays -> {
            transactionTemplate.execute(status -> {
                holidayRepository.saveAll(holidays);

                var update =  new HolidaysUpdate();
                update.setYear(year);
                update.setUpdatedAt(LocalDateTime.now());
                holidaysUpdateRepository.save(update);
                return null;
            });
        });
    }

    public List<HolidayDto> findUpcoming() {
        return holidayRepository.findUpcoming(LocalDateTime.now().toLocalDate(), Limit.of(5))
            .stream()
            .map(holidayMapper::entityToDto)
            .toList();
    }
}
