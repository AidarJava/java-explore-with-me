package ru.yandex.practicum.ewm;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.explore.comments.CommentRepository;
import ru.practicum.explore.comments.CommentServiceImpl;
import ru.practicum.explore.comments.dto.CommentDtoOut;
import ru.practicum.explore.comments.dto.CommentMapper;
import ru.practicum.explore.comments.dto.CommentSortDtoOut;
import ru.practicum.explore.comments.model.Comment;
import ru.practicum.explore.event.EventServiceImpl;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.UserServiceImpl;
import ru.practicum.explore.user.dto.UserShortDtoOut;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentMockTests {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private EventServiceImpl eventService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private JdbcTemplate jdbcTemplate;
    private List<CommentSortDtoOut> commentSortDtoOuts;
    private List<Comment> comments;
    private Comment comment;
    private CommentSortDtoOut commentSortDtoOut;
    private CommentDtoOut commentDtoOut;
    private UserShortDtoOut userDtoOut;

    @BeforeEach
    void setUp() {
        userDtoOut = new UserShortDtoOut();
        userDtoOut.setId(0);
        userDtoOut.setName("Роман");

        comment = new Comment();
        comment.setId(0);
        comment.setCreator(0);
        comment.setText("Новый комментарий");

        commentSortDtoOut = new CommentSortDtoOut();
        commentSortDtoOut.setId(0);
        commentSortDtoOut.setCreator("Роман");
        commentSortDtoOut.setEventAnnotation("Воздушные шары");
        commentSortDtoOut.setText("Новый комментарий");


        commentSortDtoOuts = new ArrayList<>();
        commentSortDtoOuts.add(commentSortDtoOut);

        commentDtoOut = new CommentDtoOut();

        comments = new ArrayList<>();
        comments.add(comment);
    }

    @SneakyThrows
    @Test
    void getAllCommentsByUserId() {
        Integer userId = 0;
        when(userService.getUser(userId)).thenReturn(userDtoOut);
        when(commentRepository.findAllByCreator(userId)).thenReturn(new ArrayList<>(comments));
        when(commentMapper.mapCommentToCommentShortDtoOut(comment)).thenReturn(commentSortDtoOut);
        List<CommentSortDtoOut> result = commentService.getAllCommentsByUserId(userId);
        assertEquals(result.getFirst(), commentSortDtoOut);
    }

    @SneakyThrows
    @Test
    void getAdminCommentsByEventId() {
        Integer commentId = 0;
        when(eventService.getPublishEventById(any())).thenReturn(new Event());
        when(commentRepository.getCommentsWithIds(any())).thenReturn(new ArrayList<>(comments));
        when(commentMapper.mapCommentToCommentDtoOut(comment)).thenReturn(commentDtoOut);
        List<CommentDtoOut> result = commentService.getAdminCommentsByEventId(commentId);
        assertEquals(result.getFirst(), commentDtoOut);
    }
}