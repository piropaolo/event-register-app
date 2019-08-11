package org.piropaolo.app.controller;

import org.piropaolo.app.domain.Event;
import org.piropaolo.app.domain.EventEntry;
import org.piropaolo.app.service.EventAccepter;
import org.piropaolo.app.service.EventProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class EventController {

    private final EventAccepter eventAccepter;
    private final EventProvider eventProvider;

    @Autowired
    public EventController(EventAccepter eventAccepter, EventProvider eventProvider) {
        this.eventAccepter = eventAccepter;
        this.eventProvider = eventProvider;
    }

    @PostMapping("/register/event")
    public Boolean register(@RequestBody Event event) {
        return eventAccepter.register(event.getEventId(), event.getUserId());
    }

    @GetMapping("/views/{eventId}")
    public EventEntry get(@PathVariable String eventId, @RequestParam Integer n) {
        return new EventEntry(eventId, eventProvider.get(eventId, n));
    }

    @PostMapping("/views/all")
    public Set<EventEntry> getAll(@RequestParam Integer n) {
        Set<EventEntry> entries = new HashSet<>();
        eventProvider.getAll(n).forEach((eventId, viewCount) -> entries.add(new EventEntry(eventId, viewCount)));
        return entries;
    }
}
