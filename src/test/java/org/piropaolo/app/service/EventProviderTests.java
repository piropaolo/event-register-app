package org.piropaolo.app.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.piropaolo.app.domain.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventProviderTests {

    @Autowired
    private EventProvider eventProvider;

    @BeforeAll
    void initAll() {
        IntStream.range(0, 5).forEach(c1 -> {
            Set<View> views = new HashSet<>();
            IntStream.range(0, 200).forEach(c2 -> {
                String userId = UUID.randomUUID().toString();
                OffsetDateTime ts = OffsetDateTime.now().minusMinutes(c2 % 20 * 5);
                views.add(new View(userId, ts));
            });
            eventProvider.getEventRepository().getEventMap().put("e" + c1, views);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "30,    e0,     15",
            "50,    e1,     25",
            "70,    e2,     35",
            "90,    e3,     45",
            "110,   e4,     55",
    })
    void Should_GetUniqueUserIdCount(Integer expectedCount, String eventId, Integer n) {
        assertEquals(expectedCount, eventProvider.get(eventId, n));
    }

    @ParameterizedTest
    @CsvSource({
            "30,    e0,     15",
            "50,    e1,     25",
            "70,    e2,     35",
            "90,    e3,     45",
            "110,   e4,     55",
    })
    void Should_GetUniqueUserIdCount_When_MultipleThreads(Integer expectedCount, String eventId, Integer n) throws InterruptedException {
        List<Integer> resultList = new ArrayList<>(1000);
        ExecutorService service = Executors.newFixedThreadPool(32);

        IntStream.range(0, 1000)
                .forEach(c -> service.execute(() ->
                        resultList.add(eventProvider.get(eventId, n))));

        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(1, resultList
                .stream()
                .distinct()
                .count());
        long wrongResultCount = resultList
                .stream()
                .filter(c -> !c.equals(expectedCount))
                .count();

        assertEquals(0, wrongResultCount);
    }

    @Test
    void Should_NotGetUniqueUserIdCount_When_EventIdIsNull() {
        assertNull(eventProvider.get(null, 30));
    }

    @Test
    void Should_NotGetUniqueUserIdCount_When_NumberIsNull() {
        assertNull(eventProvider.get("e1", null));
    }

    @Test
    void Should_NotGetUniqueUserIdCount_When_NumberLessThan0() {
        assertNull(eventProvider.get("e1", -10));
    }

    @Test
    void Should_NotGetUniqueUserIdCount_When_NumberIsGreaterThan60() {
        assertNull(eventProvider.get("e1", 70));
    }

    @ParameterizedTest
    @CsvSource({
            "30,    15",
            "50,    25",
            "70,    35",
            "90,    45",
            "110,   55",
    })
    void Should_GetAllUniqueUserIdCount(Integer expectedCount, Integer n) {
        eventProvider.getAll(n).forEach((eventId, viewCount) -> assertEquals(expectedCount, viewCount));
    }

    @ParameterizedTest
    @CsvSource({
            "30,    15",
            "50,    25",
            "70,    35",
            "90,    45",
            "110,   55",
    })
    void Should_GetAllUniqueUserIdCount_When_MultipleThreads(Integer expectedCount, Integer n) throws InterruptedException {
        List<Integer> resultList = new ArrayList<>(5000);
        ExecutorService service = Executors.newFixedThreadPool(32);

        IntStream.range(0, 1000)
                .forEach(c -> service.execute(() ->
                        eventProvider.getAll(n).forEach((eventId, viewCount) -> resultList.add(viewCount))));

        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(1, resultList
                .stream()
                .distinct()
                .count());
        long wrongResultCount = resultList
                .stream()
                .filter(c -> !c.equals(expectedCount))
                .count();

        assertEquals(0, wrongResultCount);
    }

    @Test
    void Should_NotGetAllUniqueUserIdCount_When_NumberIsNull() {
        assertNull(eventProvider.getAll(null));
    }

    @Test
    void Should_NotGetAllUniqueUserIdCount_When_NumberIsLessThan0() {
        assertNull(eventProvider.getAll(-10));
    }

    @Test
    void Should_NotGetAllUniqueUserIdCount_When_NumberIsGreaterThan60() {
        assertNull(eventProvider.getAll(70));
    }
}
