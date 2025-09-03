package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.exception.RaceAlreadyExistsException;
import com.ivangochev.raceratingapi.factory.MockDataFactory;
import com.ivangochev.raceratingapi.notification.NotificationService;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceServiceImplTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private RaceMapper raceMapper;

    @InjectMocks
    private RaceServiceImpl raceService;

    private User testUser;
    private Race testRace;
    private CreateRaceDto createRaceDto;
    private RaceDto raceDto;
    private List<RaceSummaryDto> raceSummaries;

    @BeforeEach
    void setUp() {
        // Set up test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRace = new Race();
        testRace.setId(1L);
        testRace.setName("Test Race");
        testRace.setDescription("Test Description");
        testRace.setAverageRating(new BigDecimal("4.5"));
        testRace.setEventDate(new Date());
        testRace.setAuthor(testUser);
        testRace.setVoters(new ArrayList<>());
        testRace.setCommenters(new ArrayList<>());

        createRaceDto = MockDataFactory.createTestCreateRaceDto();

        raceDto = new RaceDto();
        raceDto.setId(1L);
        raceDto.setName("Test Race");
        raceDto.setDescription("Test Description");

        RaceSummaryDto raceSummary = new RaceSummaryDto(
                1L,
                "Test Race",
                "https://anotherLogo.com/",
                BigDecimal.valueOf(10L),
                2,
                3L,
                4L,
                MockDataFactory.createTestDate()
        );
        raceSummaries = List.of(raceSummary);
    }

    @Test
    void createRace_shouldCreateAndReturnRace() {
        // Arrange
        when(raceMapper.createRaceDtoToRace(any(), any())).thenReturn(testRace);
        when(raceRepository.save(any())).thenReturn(testRace);
        when(raceMapper.RaceToRaceDto(any())).thenReturn(raceDto);
        doNothing().when(notificationService).notifyAllUsersAboutNewRace(anyLong(), anyString());

        // Act
        RaceDto result = raceService.createRace(createRaceDto, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(raceDto.getId(), result.getId());
        assertEquals(raceDto.getName(), result.getName());
        verify(raceMapper).createRaceDtoToRace(createRaceDto, testUser);
        verify(raceRepository).save(testRace);
        verify(notificationService).notifyAllUsersAboutNewRace(testRace.getId(), testRace.getName());
        verify(raceMapper).RaceToRaceDto(testRace);
    }

    @Test
    void editRace_shouldUpdateAndReturnRace() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(testRace));
        when(raceMapper.editRace(any(), any())).thenReturn(testRace);
        when(raceRepository.save(any())).thenReturn(testRace);
        when(raceMapper.RaceToRaceDto(any())).thenReturn(raceDto);

        // Act
        RaceDto result = raceService.editRace(1L, createRaceDto);

        // Assert
        assertNotNull(result);
        assertEquals(raceDto.getId(), result.getId());
        assertEquals(raceDto.getName(), result.getName());
        verify(raceRepository).findById(1L);
        verify(raceMapper).editRace(createRaceDto, testRace);
        verify(raceRepository).save(testRace);
        verify(raceMapper).RaceToRaceDto(testRace);
    }

    @Test
    void editRace_shouldThrowExceptionWhenRaceNotFound() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RaceNotFoundException.class, () -> raceService.editRace(1L, createRaceDto));
        verify(raceRepository).findById(1L);
        verifyNoMoreInteractions(raceMapper, raceRepository);
    }

    @Test
    void getAllRaces_shouldReturnAllRaceSummaries() {
        // Arrange
        when(raceRepository.findAllRaceSummaries()).thenReturn(raceSummaries);

        // Act
        List<RaceSummaryDto> result = raceService.getAllRaces();

        // Assert
        assertNotNull(result);
        assertEquals(raceSummaries.size(), result.size());
        verify(raceRepository).findAllRaceSummaries();
    }

    @Test
    void getRaceById_shouldReturnRace() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(testRace));
        when(raceMapper.RaceToRaceDto(any())).thenReturn(raceDto);

        // Act
        RaceDto result = raceService.getRaceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(raceDto.getId(), result.getId());
        assertEquals(raceDto.getName(), result.getName());
        verify(raceRepository).findById(1L);
        verify(raceMapper).RaceToRaceDto(testRace);
    }

    @Test
    void getRaceById_shouldThrowExceptionWhenRaceNotFound() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RaceNotFoundException.class, () -> raceService.getRaceById(1L));
        verify(raceRepository).findById(1L);
        verifyNoMoreInteractions(raceMapper);
    }

    @Test
    void validateRaceDoesNotExist_shouldNotThrowWhenRaceDoesNotExist() {
        // Arrange
        when(raceRepository.findRaceByName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> raceService.validateRaceDoesNotExist("New Race"));
        verify(raceRepository).findRaceByName("New Race");
    }

    @Test
    void validateRaceDoesNotExist_shouldThrowWhenRaceExists() {
        // Arrange
        when(raceRepository.findRaceByName(anyString())).thenReturn(Optional.of(testRace));

        // Act & Assert
        assertThrows(RaceAlreadyExistsException.class, () -> raceService.validateRaceDoesNotExist("Test Race"));
        verify(raceRepository).findRaceByName("Test Race");
    }

    @Test
    void isRaceOwner_shouldReturnTrueWhenUserIsOwner() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(testRace));

        // Act
        boolean result = raceService.isRaceOwner(1L, testUser);

        // Assert
        assertTrue(result);
        verify(raceRepository).findById(1L);
    }

    @Test
    void isRaceOwner_shouldReturnFalseWhenUserIsNotOwner() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);
        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(testRace));

        // Act
        boolean result = raceService.isRaceOwner(1L, differentUser);

        // Assert
        assertFalse(result);
        verify(raceRepository).findById(1L);
    }

    @Test
    void isRaceOwner_shouldReturnFalseWhenRaceNotFound() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = raceService.isRaceOwner(1L, testUser);

        // Assert
        assertFalse(result);
        verify(raceRepository).findById(1L);
    }

    @Test
    void deleteRace_shouldDeleteRace() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.of(testRace));
        doNothing().when(raceRepository).delete(any());

        // Act
        raceService.deleteRace(1L);

        // Assert
        verify(raceRepository).findById(1L);
        verify(raceRepository).delete(testRace);
    }

    @Test
    void deleteRace_shouldThrowExceptionWhenRaceNotFound() {
        // Arrange
        when(raceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> raceService.deleteRace(1L));
        verify(raceRepository).findById(1L);
        verifyNoMoreInteractions(raceRepository);
    }
}