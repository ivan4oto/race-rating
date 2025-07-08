package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findRaceByName(String name);

    @Query("""
    SELECT new com.ivangochev.raceratingapi.race.dto.RaceSummaryDto(
            r.id,
            r.name,
            r.logoUrl,
            r.averageRating,
            r.ratingsCount,
            SIZE(r.raceComments),
            SIZE(r.voters),
            r.eventDate
        )
        FROM Race r
    """)
    List<RaceSummaryDto> findAllRaceSummaries();
    Optional<Race> deleteRaceById(Long raceId);
}
