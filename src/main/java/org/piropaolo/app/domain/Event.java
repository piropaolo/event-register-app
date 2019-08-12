package org.piropaolo.app.domain;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class Event {

    private final String eventId;
    private final String userId;

    public Event(final String eventId, final String userId) {
        this.eventId = checkNotNull(eventId);
        this.userId = checkNotNull(userId);
    }
}
