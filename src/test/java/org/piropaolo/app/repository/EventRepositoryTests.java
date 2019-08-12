package org.piropaolo.app.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.piropaolo.app.domain.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EventRepositoryTests {

    @Autowired
    EventRepository eventRepository;

    @BeforeEach
    void init() {
        /* 5 distinct eventIds */
        IntStream.range(0, 5).forEach(c1 -> {
            Set<View> views = new HashSet<>();
            /* 200 distinct userIds */
            IntStream.range(0, 200).forEach(c2 -> {
                String userId = UUID.randomUUID().toString();
                /* Set view timestamps in range from 95 min before to now. */
                OffsetDateTime ts = OffsetDateTime.now().minusMinutes(c2 % 20 * 5);
                views.add(new View(userId, ts));
            });
            eventRepository.getEventMap().put("e" + c1, views);
        });

        /* other 5 distinct eventIds */
        IntStream.range(5, 10).forEach(c1 -> {
            Set<View> views = new HashSet<>();
            /* 200 distinct userIds */
            IntStream.range(0, 200).forEach(c2 -> {
                String userId = UUID.randomUUID().toString();
                /* Set view timestamps in range from 155 min before to 60 min before.
                 * This ensures events(5-9) and their views should be cleared. */
                OffsetDateTime ts = OffsetDateTime.now().minusMinutes(c2 % 20 * 5 + 60);
                views.add(new View(userId, ts));
            });
            eventRepository.getEventMap().put("e" + c1, views);
        });
    }

    @Test
    void Should_RemoveOldViews() {
        assertEquals(10, eventRepository.getEventMap().size());
        eventRepository.getEventMap().forEach((eventId, views) -> assertEquals(200, views.size()));

        eventRepository.removeOldViews();

        assertEquals(10, eventRepository.getEventMap().size());
        IntStream.range(0, 5).forEach(c -> assertEquals(120, eventRepository.getEventMap().get("e" + c).size()));
        IntStream.range(6, 10).forEach(c -> assertEquals(0, eventRepository.getEventMap().get("e" + c).size()));
    }
    @Test
    void Should_RemoveOldViewsAndEvents() {
        assertEquals(10, eventRepository.getEventMap().size());
        eventRepository.getEventMap().forEach((eventId, views) -> assertEquals(200, views.size()));

        eventRepository.removeOldViews();
        eventRepository.removeEventsWithNoViews();

        assertEquals(5, eventRepository.getEventMap().size());
        eventRepository.getEventMap().forEach((eventId, views) -> assertEquals(120, views.size()));
    }

    @Test
    void Should_ReadProperties() {
        assertEquals(10, eventRepository.getInitialCapacity());
        assertEquals(0.5f, eventRepository.getLoadFactor());
        assertEquals(32, eventRepository.getConcurrencyLevel());
    }

    @AfterEach
    void tearDown() {
        eventRepository.getEventMap().clear();
    }
}
