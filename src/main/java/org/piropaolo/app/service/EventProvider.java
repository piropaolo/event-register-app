package org.piropaolo.app.service;

import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.piropaolo.app.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Getter
@Slf4j
public class EventProvider {

    private final EventRepository eventRepository;

    @Autowired
    public EventProvider(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Integer get(String eventId, Integer n) {
        try {
            checkNotNull(eventId);
            checkNotNull(n);
            checkArgument(n > 0 && n < 60);
        } catch (NullPointerException e) {
            log.error("Get method arguments cannot be null.", e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Number must be between 0 and 60 minutes");
            return null;
        }

        return Ints.checkedCast(eventRepository.getEventMap().get(eventId)
                .stream()
                .filter(view -> OffsetDateTime
                        .now()
                        .minusMinutes(n)
                        .isBefore(view.getTs()))
                .count());
    }

    public Map<String, Integer> getAll(Integer n) {
        try {
            checkNotNull(n);
            checkArgument(n > 0 && n < 60);
        } catch (NullPointerException e) {
            log.error("GetAll method argument cannot be null.", e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Number must be between 0 and 60 minutes");
            return null;
        }

        Map<String, Integer> resultMap = new HashMap<>();

        eventRepository.getEventMap().forEach((eventId, views) -> resultMap.put(eventId, Ints.checkedCast(views
                .stream()
                .filter(view -> OffsetDateTime
                        .now()
                        .minusMinutes(n)
                        .isBefore(view.getTs()))
                .count())));

        return resultMap;
    }
}
