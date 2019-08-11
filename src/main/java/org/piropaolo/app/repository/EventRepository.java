package org.piropaolo.app.repository;

import lombok.Getter;
import org.piropaolo.app.domain.View;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Getter
public class EventRepository {

    private Map<String, Set<View>> eventMap;

    public EventRepository() {
        eventMap = new ConcurrentHashMap<>();
    }

    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 5 * 60 * 1000)
    void cleanUp() {
        removeOldViews();
        removeEventsWithNoViews();
    }

    void removeOldViews() {
        eventMap.forEach((eventId, views) ->
                eventMap.computeIfPresent(eventId, (key, value) -> value
                        .stream()
                        .filter(view -> OffsetDateTime.now()
                                .minusMinutes(60)
                                .isBefore(view.getTs()))
                        .collect(Collectors.toSet())));
    }

    void removeEventsWithNoViews() {
        eventMap.forEach((eventId, views) -> {
            if (views.isEmpty())
                eventMap.remove(eventId, views);
        });
    }
}
