package com.ivangochev.raceratingapi.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RaceCreatedEvent extends ApplicationEvent {
    private final Long raceId;
    private final Long authorId;
    private final String raceName;


    public RaceCreatedEvent(Object source, Long id, Long authorId, String name) {
        super(source);
        this.raceId = id;
        this.authorId = authorId;
        this.raceName = name;
    }
}
