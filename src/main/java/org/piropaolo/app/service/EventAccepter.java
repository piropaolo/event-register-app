package org.piropaolo.app.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.piropaolo.app.domain.View;
import org.piropaolo.app.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Getter
@Slf4j
public class EventAccepter {

    private final EventRepository eventRepository;

    @Autowired
    public EventAccepter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Boolean register(String eventId, String userId) {
        try {
            checkNotNull(eventId);
            checkNotNull(userId);
        } catch (NullPointerException e) {
            log.error("Register method arguments cannot be null.", e);
            return Boolean.FALSE;
        }

        eventRepository.getEventMap().merge(
                eventId,
                Stream.of(new View(userId)).collect(Collectors.toSet()),
                (oldSet, newView) -> Stream.concat(oldSet.stream(), newView.stream()).collect(Collectors.toSet())
        );

        return Boolean.TRUE;
    }
}
