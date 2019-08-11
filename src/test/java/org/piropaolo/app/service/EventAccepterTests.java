package org.piropaolo.app.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EventAccepterTests {

    @Autowired
    EventAccepter eventAccepter;

    @Test
    void Should_RegisterEvent() {
        assertTrue(eventAccepter.register("e1", "b16e6dc4-1672-4068-ad2f-54be1c39e18c"));
    }

    @Test
    void Should_RegisterEvents_When_MultipleThreads() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(32);

        IntStream.range(0, 1000)
                .forEach(c -> service.execute(() ->
                        eventAccepter.register("e1", "b16e6dc4-1672-4068-ad2f-54be1c39e18c")));

        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(1, eventAccepter.getEventRepository().getEventMap().size());
        assertEquals(1000, eventAccepter.getEventRepository().getEventMap().get("e1").size());
    }

    @Test
    void Should_RegisterEvents_When_MultipleThreadsAndMultipleEvents() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(32);

        IntStream.range(0, 10)
                .forEach(c1 -> {
                    String userId = UUID.randomUUID().toString();
                    IntStream.range(0, 100).forEach(c2 -> service.execute(() ->
                            eventAccepter.register("e" + c1, userId)));
                });

        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(10, eventAccepter.getEventRepository().getEventMap().size());
        eventAccepter.getEventRepository().getEventMap()
                .forEach((eventId, views) -> assertEquals(100, views.size()));
    }

    @Test
    void Should_RegisterEvents_When_MultipleThreadsAndMultipleEventsAndMultipleUsers() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(32);

        IntStream.range(0, 5)
                .forEach(c1 -> IntStream.range(0, 200).forEach(c2 -> service.execute(() ->
                        eventAccepter.register("e" + c1, UUID.randomUUID().toString()))));

        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(5, eventAccepter.getEventRepository().getEventMap().size());
        eventAccepter.getEventRepository().getEventMap()
                .forEach((eventId, views) -> assertEquals(200, views.size()));
    }

    @Test
    void Should_NotRegisterEvent_WhenEventIdIsNull() {
        assertFalse(eventAccepter.register(null, "b16e6dc4-1672-4068-ad2f-54be1c39e18c"));
    }

    @Test
    void Should_NotRegisterEvent_WhenUserIdIsNull() {
        assertFalse(eventAccepter.register("e1", null));
    }

    @AfterEach
    void tearDown() {
        eventAccepter.getEventRepository().getEventMap().clear();
    }

}
