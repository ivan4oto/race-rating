package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVote;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteRepository;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteResponseDTO;
import com.ivangochev.raceratingapi.racecomment.vote.UserCommentVoteStatus;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RaceCommentServiceImplTest {

    @Mock private RaceCommentRepository commentRepository;
    @Mock private UserRepository userRepository;
    @Mock private RaceCommentMapper commentMapper;
    @Mock private RaceRepository raceRepository;
    @Mock private CommentVoteRepository commentVoteRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RaceCommentServiceImpl service;

    private User mockUser;
    private Race mockRace;
    private RaceComment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setCommentedForRaces(new ArrayList<>(List.of()));

        mockRace = new Race();
        mockRace.setId(100L);

        mockComment = new RaceComment();
        mockComment.setId(10L);
        mockComment.setRace(mockRace);
    }

    @Test
    void getRaceCommentsByRaceId_shouldReturnList() {
        List<RaceCommentWithVotesDto> mockList = List.of(new RaceCommentWithVotesDto());
        when(commentRepository.findCommentsWithVoteCountsByRaceId(100L)).thenReturn(mockList);

        List<RaceCommentWithVotesDto> result = service.getRaceCommentsByRaceId(100L);

        assertEquals(1, result.size());
        verify(commentRepository).findCommentsWithVoteCountsByRaceId(100L);
    }

    @Test
    void createRaceComment_shouldCreateAndReturnDto_whenNotYetCommented() {
        RaceCommentRequestDTO dto = new RaceCommentRequestDTO();
        RaceComment savedComment = new RaceComment();
        savedComment.setRace(mockRace);

        when(commentRepository.existsByAuthorIdAndRaceId(mockUser.getId(), mockRace.getId())).thenReturn(false);
        when(raceRepository.findById(mockRace.getId())).thenReturn(Optional.of(mockRace));
        when(commentMapper.toRaceComment(dto, mockUser, mockRace)).thenReturn(mockComment);
        when(commentRepository.save(mockComment)).thenReturn(savedComment);
        when(commentMapper.toRaceCommentWithVotesDto(savedComment)).thenReturn(new RaceCommentWithVotesDto());

        RaceCommentWithVotesDto result = service.createRaceComment(dto, mockUser, mockRace.getId());

        assertNotNull(result);
        verify(commentRepository).save(mockComment);
    }

    @Test
    void createRaceComment_shouldThrow_whenUserAlreadyCommented() {
        when(commentRepository.existsByAuthorIdAndRaceId(mockUser.getId(), mockRace.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createRaceComment(new RaceCommentRequestDTO(), mockUser, mockRace.getId()));
    }

    @Test
    void voteComment_shouldCreateNewVote_whenNoExistingVote() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(mockComment));
        when(commentVoteRepository.findByCommentAndVoter(mockComment, mockUser)).thenReturn(Optional.empty());

        CommentVoteResponseDTO response = service.voteComment(10L, true, mockUser);

        assertTrue(response.isVoteRegistered());
        assertTrue(response.getCurrentVote());

        ArgumentCaptor<CommentVote> voteCaptor = ArgumentCaptor.forClass(CommentVote.class);
        verify(commentVoteRepository).save(voteCaptor.capture());

        CommentVote savedVote = voteCaptor.getValue();
        assertEquals(mockComment, savedVote.getComment());
        assertEquals(mockUser, savedVote.getVoter());
        assertTrue(savedVote.isUpvote());
    }

    @Test
    void voteComment_shouldNotChangeVote_whenSameVoteExists() {
        CommentVote vote = new CommentVote();
        vote.setUpvote(true);

        when(commentRepository.findById(10L)).thenReturn(Optional.of(mockComment));
        when(commentVoteRepository.findByCommentAndVoter(mockComment, mockUser)).thenReturn(Optional.of(vote));

        CommentVoteResponseDTO response = service.voteComment(10L, true, mockUser);

        assertFalse(response.isVoteRegistered());
    }

    @Test
    void voteComment_shouldChangeVoteDirection_whenDifferentVoteExists() {
        CommentVote vote = new CommentVote();
        vote.setUpvote(false);

        when(commentRepository.findById(10L)).thenReturn(Optional.of(mockComment));
        when(commentVoteRepository.findByCommentAndVoter(mockComment, mockUser)).thenReturn(Optional.of(vote));

        CommentVoteResponseDTO response = service.voteComment(10L, true, mockUser);

        assertTrue(response.isVoteRegistered());
        assertTrue(vote.isUpvote());
    }

    @Test
    void getCommentVotesByCommentId_shouldReturnStatuses() {
        List<Long> ids = List.of(1L, 2L);
        UserCommentVoteStatus mockStatus = mock(UserCommentVoteStatus.class);
        List<UserCommentVoteStatus> mockList = List.of(mockStatus);

        when(commentVoteRepository.findByVoterIdAndCommentIdIn(1L, ids)).thenReturn(mockList);

        List<UserCommentVoteStatus> result = service.getCommentVotesByCommentId(1L, ids);

        assertEquals(1, result.size());
    }

    @Test
    void deleteComment_shouldDeleteCommentAndUpdateUser() {
        mockUser.getCommentedForRaces().add(mockRace);

        when(commentRepository.existsById(10L)).thenReturn(true);
        service.deleteComment(10L, mockRace.getId(), mockUser);

        verify(userRepository).save(mockUser);
        verify(commentRepository).deleteById(10L);
    }

    @Test
    void deleteComment_shouldReturnFalse_whenCommentNotFound() {
        when(commentRepository.existsById(10L)).thenReturn(false);

        boolean result = service.deleteComment(10L, 100L, mockUser);

        assertFalse(result);
        verify(commentRepository, never()).deleteById(ArgumentMatchers.any());
    }
}