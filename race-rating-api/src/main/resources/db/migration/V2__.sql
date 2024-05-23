CREATE TABLE race_available_distances
(
    race_id             BIGINT  NOT NULL,
    available_distances INTEGER NOT NULL
);

ALTER TABLE race_available_distances
    ADD CONSTRAINT fk_race_availabledistances_on_race FOREIGN KEY (race_id) REFERENCES races (id);

ALTER TABLE race_terrain_tags
    DROP CONSTRAINT race_terrain_tags_pkey;