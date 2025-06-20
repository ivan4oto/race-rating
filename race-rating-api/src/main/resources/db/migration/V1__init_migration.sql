CREATE TABLE comment_vote
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    comment_id BIGINT                                  NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    upvote     BOOLEAN                                 NOT NULL,
    voted_at   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_commentvote PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    race_id      BIGINT                                  NOT NULL,
    comment_text VARCHAR(2000)                           NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    author_id    BIGINT                                  NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE race_available_distances
(
    race_id             BIGINT NOT NULL,
    available_distances INTEGER
);

CREATE TABLE race_terrain_tags
(
    race_id      BIGINT       NOT NULL,
    terrain_tags VARCHAR(255) NOT NULL
);

CREATE TABLE races
(
    id                         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ratings_count              INTEGER                                 NOT NULL,
    name                       VARCHAR(255)                            NOT NULL,
    description                VARCHAR(2000),
    average_rating             DECIMAL(2, 1)                           NOT NULL,
    average_trace_score        DECIMAL(2, 1)                           NOT NULL,
    average_vibe_score         DECIMAL(2, 1)                           NOT NULL,
    average_organization_score DECIMAL(2, 1)                           NOT NULL,
    average_location_score     DECIMAL(2, 1)                           NOT NULL,
    average_value_score        DECIMAL(2, 1)                           NOT NULL,
    latitude                   DECIMAL(10, 6),
    longitude                  DECIMAL(10, 6),
    website_url                VARCHAR(255),
    logo_url                   VARCHAR(255),
    distance                   DECIMAL                                 NOT NULL,
    elevation                  INTEGER                                 NOT NULL,
    event_date                 TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    created_by_user_id         BIGINT,
    CONSTRAINT pk_races PRIMARY KEY (id)
);

CREATE TABLE ratings
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    race_id            BIGINT                                  NOT NULL,
    author_id          BIGINT                                  NOT NULL,
    trace_score        INTEGER                                 NOT NULL,
    vibe_score         INTEGER                                 NOT NULL,
    organization_score INTEGER                                 NOT NULL,
    location_score     INTEGER                                 NOT NULL,
    value_score        INTEGER                                 NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_ratings PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    token       VARCHAR(255),
    expiry_date TIMESTAMP WITHOUT TIME ZONE,
    user_id     BIGINT,
    CONSTRAINT pk_refreshtoken PRIMARY KEY (id)
);

CREATE TABLE users
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username    VARCHAR(255),
    password    VARCHAR(255),
    name        VARCHAR(255),
    email       VARCHAR(255),
    role        VARCHAR(255),
    image_url   VARCHAR(255),
    provider    VARCHAR(255),
    provider_id VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_commented_for_races
(
    user_id                BIGINT NOT NULL,
    commented_for_races_id BIGINT NOT NULL
);

CREATE TABLE users_voted_for_races
(
    user_id            BIGINT NOT NULL,
    voted_for_races_id BIGINT NOT NULL
);

ALTER TABLE comments
    ADD CONSTRAINT uc_007f764d54f361a67e083d84d UNIQUE (author_id, race_id);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_77584fbe74cc86922be2a3560 UNIQUE (username);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refreshtoken_user UNIQUE (user_id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_RACE FOREIGN KEY (race_id) REFERENCES races (id);

ALTER TABLE comment_vote
    ADD CONSTRAINT FK_COMMENTVOTE_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE comment_vote
    ADD CONSTRAINT FK_COMMENTVOTE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE races
    ADD CONSTRAINT FK_RACES_ON_CREATED_BY_USER FOREIGN KEY (created_by_user_id) REFERENCES users (id);

ALTER TABLE ratings
    ADD CONSTRAINT FK_RATINGS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE ratings
    ADD CONSTRAINT FK_RATINGS_ON_RACE FOREIGN KEY (race_id) REFERENCES races (id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESHTOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE race_available_distances
    ADD CONSTRAINT fk_race_availabledistances_on_race FOREIGN KEY (race_id) REFERENCES races (id);

ALTER TABLE race_terrain_tags
    ADD CONSTRAINT fk_race_terraintags_on_race FOREIGN KEY (race_id) REFERENCES races (id);

ALTER TABLE users_commented_for_races
    ADD CONSTRAINT fk_usecomforrac_on_race FOREIGN KEY (commented_for_races_id) REFERENCES races (id);

ALTER TABLE users_commented_for_races
    ADD CONSTRAINT fk_usecomforrac_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users_voted_for_races
    ADD CONSTRAINT fk_usevotforrac_on_race FOREIGN KEY (voted_for_races_id) REFERENCES races (id);

ALTER TABLE users_voted_for_races
    ADD CONSTRAINT fk_usevotforrac_on_user FOREIGN KEY (user_id) REFERENCES users (id);