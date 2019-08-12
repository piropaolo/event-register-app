package org.piropaolo.app.domain;

import lombok.Getter;

import java.time.OffsetDateTime;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class View {

    private final String userId;
    private final OffsetDateTime ts;

    public View(final String userId) {
        this.userId = checkNotNull(userId);
        this.ts = OffsetDateTime.now();
    }

    public View(final String userId, final OffsetDateTime ts) {
        this.userId = checkNotNull(userId);
        this.ts = checkNotNull(ts);
    }
}
