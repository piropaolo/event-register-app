package org.piropaolo.app.repository;

import lombok.Getter;
import org.piropaolo.app.domain.View;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Getter
public class EventRepository {

    private Map<String, Set<View>> eventMap;

    public EventRepository() {
        eventMap = new ConcurrentHashMap<>();
    }
}
