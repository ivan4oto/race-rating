package com.ivangochev.raceratingapi.racecomment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivangochev.raceratingapi.config.CustomTestConfig;
import com.ivangochev.raceratingapi.factory.MockDataFactory;
import com.ivangochev.raceratingapi.security.TokenAuthenticationFilter;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import com.ivangochev.raceratingapi.user.dto.UserResponseDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CustomTestConfig.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private RaceCommentService raceCommentService;
    @MockBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllCommentsForRace_ReturnsOK() throws Exception {
        RaceCommentResponseDTO responseDtoOne = new RaceCommentResponseDTO();
        responseDtoOne.setId(1L);
        responseDtoOne.setRaceId(1L);
        responseDtoOne.setCommentText("A random comment!");
        responseDtoOne.setCreatedAt(MockDataFactory.createTestDate());
        responseDtoOne.setAuthor(new UserResponseDTO(
                1L,
                "username",
                "Ivan",
                "ivan@abv.bg",
                "http://image.com/image.jpeg"
        ));

        RaceCommentResponseDTO responseDtoTwo = new RaceCommentResponseDTO();
        responseDtoTwo.setId(2L);
        responseDtoTwo.setRaceId(2L);
        responseDtoTwo.setCommentText("Another comment!");
        responseDtoTwo.setCreatedAt(MockDataFactory.createTestDate());
        responseDtoTwo.setAuthor(new UserResponseDTO(
                2L,
                "usertwo",
                "Pesho",
                "pesho@abv.bg",
                "http://randomsite.com/avatar.jpeg"
        ));


        List<RaceCommentResponseDTO> raceCommentResponseDTOS = new ArrayList<>();
        raceCommentResponseDTOS.add(responseDtoOne);
        raceCommentResponseDTOS.add(responseDtoTwo);

        when(raceCommentService.getRaceCommentsByRaceId(1L)).thenReturn(raceCommentResponseDTOS);
        mockMvc.perform(get("/public/comments/race/{raceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentText").value(responseDtoOne.getCommentText()))
                .andExpect(jsonPath("$[1].commentText").value(responseDtoTwo.getCommentText()));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "ivan", userDetailsServiceBeanName = "userDetailsServiceMock")
    public void testCreateComment_ReturnsCreated() throws Exception {
        User user = MockDataFactory.createTestUser();
        when(userService.validateAndGetUserByUsername("ivan")).thenReturn(user);

        RaceCommentResponseDTO responseDTO = new RaceCommentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRaceId(1L);
        responseDTO.setCommentText("A random comment!");
        responseDTO.setCreatedAt(MockDataFactory.createTestDate());
        responseDTO.setAuthor(new UserResponseDTO(
                1L,
                "username",
                "Ivan",
                "ivan@abv.bg",
                "http://image.com/image.jpeg"
        ));
        RaceCommentRequestDTO requestDTO = new RaceCommentRequestDTO();
        requestDTO.setCommentText(responseDTO.getCommentText());

        when(raceCommentService.createRaceComment(any(), any(), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(post("/public/comments/{raceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentText").value(responseDTO.getCommentText()));
    }
}