package ru.practicum.explore.comments.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.explore.comments.model.Comment;
import ru.practicum.explore.event.EventService;
import ru.practicum.explore.event.dto.EventMapper;
import ru.practicum.explore.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserService userService;
    private final EventService eventService;
    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    public Comment mapCommentDtoInToComment(CommentDtoIn commentDtoIn) {
        Comment comment = new Comment();
        comment.setText(commentDtoIn.getText());
        return comment;
    }

    public CommentSortDtoOut mapCommentToCommentShortDtoOut(Comment comment) {
        CommentSortDtoOut commentSortDtoOut = new CommentSortDtoOut();
        commentSortDtoOut.setId(comment.getId());
        commentSortDtoOut.setCreator(userService.getUser(comment.getCreator()).getName());
        List<Integer> eventId = jdbcTemplate.query("SELECT ce.event_id FROM comments_events AS ce WHERE ce.comment_id = ?", (rs, rowNum) -> rs.getInt("event_id"), comment.getId());
        commentSortDtoOut.setEventAnnotation(eventService.getPublishEventById(eventId.getFirst()).getAnnotation());
        commentSortDtoOut.setText(comment.getText());
        return commentSortDtoOut;
    }

    public CommentDtoOut mapCommentToCommentDtoOut(Comment comment) {
        CommentDtoOut commentDtoOut = new CommentDtoOut();
        commentDtoOut.setId(comment.getId());
        commentDtoOut.setUserShortDtoOut(userService.getUser(comment.getCreator()));
        List<Integer> eventId = jdbcTemplate.query("SELECT ce.event_id FROM comments_events AS ce WHERE ce.comment_id = ?", (rs, rowNum) -> rs.getInt("event_id"), comment.getId());
        commentDtoOut.setEventShortDtoOut(eventMapper.mapEventToEventShortDtoOut(eventService.getPublishEventById(eventId.getFirst())));
        commentDtoOut.setText(comment.getText());
        return commentDtoOut;
    }
}
