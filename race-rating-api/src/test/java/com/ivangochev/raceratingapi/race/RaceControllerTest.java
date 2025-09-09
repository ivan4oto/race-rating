package com.ivangochev.raceratingapi.race;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.config.AwsProperties;
import com.ivangochev.raceratingapi.config.CustomTestConfig;
import com.ivangochev.raceratingapi.factory.MockDataFactory;
import com.ivangochev.raceratingapi.logo.LogoProcessor;
import com.ivangochev.raceratingapi.race.dto.CreateRaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceDto;
import com.ivangochev.raceratingapi.race.dto.RaceSummaryDto;
import com.ivangochev.raceratingapi.security.TokenAuthenticationFilter;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.utils.aws.S3PresignedUrlGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RaceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomTestConfig.class)
class RaceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RaceService raceService;
    @MockBean
    private UserService userService;
    @MockBean
    private AwsProperties awsProperties;
    @MockBean
    S3PresignedUrlGenerator s3PresignedUrlGenerator;
    @MockBean
    private S3Client s3Client;
    @MockBean
    private S3ObjectMapper s3ObjectMapper;
    @MockBean
    private LogoProcessor logoProcessor;
    @MockBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllRaces_ReturnsOK() throws Exception {
        List<RaceSummaryDto> raceDtos = new ArrayList<>();
        RaceSummaryDto raceDto_1 = new RaceSummaryDto(
                3L,
                "myRace",
                "https://defaultlogo.com/",
                BigDecimal.valueOf(10L),
                2,
                3L,
                4L,
                MockDataFactory.createTestDate()
        );
        RaceSummaryDto raceDto_2 = new RaceSummaryDto(
                5L,
                "anotherRace",
                "https://anotherLogo.com/",
                BigDecimal.valueOf(10L),
                2,
                3L,
                4L,
                MockDataFactory.createTestDate()
        );
        raceDtos.add(raceDto_1);
        raceDtos.add(raceDto_2);

        when(raceService.getAllRaces()).thenReturn(raceDtos);
        mockMvc.perform(get("/api/race/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[1].id").value(5L));
    }

    @Test
    public void testGetRaceById_ReturnsOK() throws Exception {
        Long raceId = 1L;
        RaceDto raceDto = new RaceDto();
        raceDto.setId(1L);
        raceDto.setAverageRating(new BigDecimal(5));
        when(raceService.getRaceById(raceId)).thenReturn(raceDto);
        mockMvc.perform(get("/api/race/{raceId}", raceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.averageRating").value(new BigDecimal(5)));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "ivan", userDetailsServiceBeanName = "userDetailsServiceMock")
    public void testCreateRace_ReturnsCreated() throws Exception {
        CreateRaceDto createRaceDto = MockDataFactory.createTestCreateRaceDto();
        User user = MockDataFactory.createTestUser();
        when(userService.validateAndGetUserByUsername("ivan")).thenReturn(user);
        when(raceService.createRace(any(), any())).thenReturn(MockDataFactory.createTestRaceDto());
        mockMvc.perform(post("/api/race")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRaceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("raceName"));
        verify(raceService, times(1)).validateRaceDoesNotExist("myRace");
        verify(raceService, times(1)).createRace(createRaceDto, user);
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "ivan", userDetailsServiceBeanName = "userDetailsServiceMock")
    public void testUpdateRace_UserIsNotRaceOwner_ReturnsForbidden() throws Exception {
        User user = MockDataFactory.createTestUser();
        user.setRole("USER");
        CreateRaceDto createRaceDto = MockDataFactory.createTestCreateRaceDto();
        when(userService.validateAndGetUserByUsername("ivan")).thenReturn(user);
        mockMvc.perform(put("/api/race/{raceId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRaceDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "ivan", userDetailsServiceBeanName = "userDetailsServiceMock")
    public void testUpdateRace_UserIsOwner_ReturnsOK() throws Exception {
        User user = MockDataFactory.createTestUser();
        user.setRole("USER");
        CreateRaceDto createRaceDto = MockDataFactory.createTestCreateRaceDto();
        when(raceService.isRaceOwner(1L, user)).thenReturn(Boolean.TRUE);
        when(userService.validateAndGetUserByUsername("ivan")).thenReturn(user);

        mockMvc.perform(put("/api/race/{raceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRaceDto)))
                .andExpect(status().isOk());
        verify(raceService, times(1)).editRace(1L, createRaceDto);
    }

    @Test
    public void testGetPresignedUrl_ReturnsOK() throws Exception {
        Map<String, List<String>> mockRequestBody = new HashMap<>();
        mockRequestBody.put("objectKeys", List.of("object_one", "object_two"));

        String presignedUrlOne = "http://somerandomurl.com";
        String presignedUrlTwo = "http://anotherurl.bg";

        when(s3PresignedUrlGenerator.generatePresignedUrl(any(), eq("object_one"), anyInt())).thenReturn(new URI("http://somerandomurl.com").toURL());
        when(s3PresignedUrlGenerator.generatePresignedUrl(any(), eq("object_two"), anyInt())).thenReturn(new URI("http://anotherurl.bg").toURL());
        mockMvc.perform(post("/api/get-presigned-urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequestBody)))
                        .andExpect(jsonPath("$.object_one").value(presignedUrlOne))
                        .andExpect(jsonPath("$.object_two").value(presignedUrlTwo));
    }
}