package org.piropaolo.app.domain;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class EventEntry {

    private final String eventId;
    private final Integer views;

    public EventEntry(final String eventId, final Integer views) {
        this.eventId = checkNotNull(eventId);
        this.views = checkNotNull(views);
    }
}
