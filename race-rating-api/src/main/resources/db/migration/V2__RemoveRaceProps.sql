ALTER TABLE race_available_distances
    DROP CONSTRAINT fk_race_availabledistances_on_race;

ALTER TABLE race_terrain_tags
    DROP CONSTRAINT fk_race_terraintags_on_race;

DROP TABLE race_available_distances CASCADE;

DROP TABLE race_terrain_tags CASCADE;

ALTER TABLE races
    DROP COLUMN distance;

ALTER TABLE races
    DROP COLUMN elevation;