package org.piropaolo.app.repository;

import lombok.Getter;
import org.piropaolo.app.domain.View;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Getter
public class EventRepository {

    private Map<String, Set<View>> eventMap;
    private int initialCapacity;
    private float loadFactor;
    private int concurrencyLevel;

    private void readProperties() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties mapProperties = new Properties();

        try {
            mapProperties.load(new FileInputStream(rootPath + "map.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialCapacity = Integer.parseInt(mapProperties.getProperty("initialCapacity"));
        loadFactor = Float.parseFloat(mapProperties.getProperty("loadFactor"));
        concurrencyLevel = Integer.parseInt(mapProperties.getProperty("concurrencyLevel"));
    }

    public EventRepository() {
        readProperties();
        eventMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
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
