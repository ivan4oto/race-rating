package com.ivangochev.raceratingapi.runner;

import com.ivangochev.raceratingapi.logo.LogoProcessor;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RaceLogoMigrationJob implements CommandLineRunner {
    private final RaceRepository raceRepository;
    private final LogoProcessor logoProcessor;
    @Value("${migration.job.logoS3Migration.enabled:false}")
    private boolean enabled;


    public RaceLogoMigrationJob(RaceRepository raceRepository, LogoProcessor logoProcessor) {
        this.raceRepository = raceRepository;
        this.logoProcessor = logoProcessor;
    }


    @Override
    public void run(String... args) throws Exception {
        if (!enabled) return;

        log.info("Starting migration of race logos from S3 to database");
        List<Race> races = raceRepository.findAll();

        for (Race race : races) {
            if (race.getLogoUrl() != null && !race.getLogoUrl().isBlank()) {
                try {
                    log.info("Processing race ID: " + race.getId());
                    logoProcessor.processAndUploadLogoAsync(race.getLogoUrl(), race.getId()).get(); // Wait for it (not async in this context)
                } catch (Exception e) {
                    log.error("Error processing race ID: " + race.getId(), e);
                }
            }
        }
    }
}
