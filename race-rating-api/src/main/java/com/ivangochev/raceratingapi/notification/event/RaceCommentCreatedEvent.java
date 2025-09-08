package com.ivangochev.raceratingapi.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RaceCommentCreatedEvent extends ApplicationEvent {
    private final Long commentId;
    private final Long raceId;
    private final Long authorId;

    public RaceCommentCreatedEvent(Object source, Long commentId, Long raceId, Long authorId) {
        super(source);
        this.commentId = commentId;
        this.raceId = raceId;
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "RaceCommentCreatedEvent{" +
                "commentId=" + commentId +
                ", raceId=" + raceId +
                ", authorId=" + authorId +
                '}';
    }
}