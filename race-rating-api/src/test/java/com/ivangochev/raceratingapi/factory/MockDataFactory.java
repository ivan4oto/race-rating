package com.ivangochev.raceratingapi.factory;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.user.User;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class MockDataFactory {
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("ivan");
        user.setPassword("password");
        user.setEmail("ivan@abv.bg");
        user.setName("ivan");
        user.setCommentedForRaces(List.of());
        user.setRole("ADMIN");
        user.setImageUrl("kdowa");
        user.setProvider(OAuth2Provider.GOOGLE);
        user.setProviderId("3");
        return user;
    }

    public static Race createTestRace() {
        Race race = new Race();
        race.setId(1L);
        race.setDistance(new BigDecimal(100));
        race.setElevation(10500);
        race.setAverageValueScore(new BigDecimal("5.0"));
        race.setAverageVibeScore(new BigDecimal("4.0"));
        race.setAverageTraceScore(new BigDecimal("3.0"));
        race.setAverageOrganizationScore(new BigDecimal("2.0"));
        race.setAverageLocationScore(new BigDecimal("1.0"));
        race.setRatingsCount(10);
        race.setName("raceName");
        race.setDescription("description");
        race.setAvailableDistances(new HashSet<>());
        race.setTerrainTags(new HashSet<>());
        race.setLogoUrl("logo.url");
        race.setWebsiteUrl("website.url");
        race.setLatitude(new BigDecimal("42.00"));
        race.setLongitude(new BigDecimal("39.00"));
        race.setAverageRating(new BigDecimal("3.5"));
        race.setAuthor(createTestUser());
        race.setEventDate(new Date());
        return race;
    }

    public static CreateRaceDto createTestCreateRaceDto() {
        return new CreateRaceDto(
                "myRace",
                "description",
                new BigDecimal(39),
                new BigDecimal(42),
                "www.abv.bg",
                "logo.com",
                new HashSet<Integer>(),
                new HashSet<String>(),
                new BigDecimal(10),
                10500,
                createTestDate()
        );
    }

    /**
     * Generates Date object to use in tests.
     * This method generates a Date object with timezone.
     * This is necessary to preserve accuracy when parsing with ObjectMapper.
     */
    public static Date createTestDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("EET"));
        String dateStr = "Thu Oct 24 03:00:00 EEST 2024";
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("Error parsing date in TestUserFactory.java createTestDate factory method.");
        }
        return null;
    }
}
