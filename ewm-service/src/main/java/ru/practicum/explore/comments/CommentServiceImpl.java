package ru.practicum.explore.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.comments.dto.CommentDtoIn;
import ru.practicum.explore.comments.dto.CommentDtoOut;
import ru.practicum.explore.comments.dto.CommentMapper;
import ru.practicum.explore.comments.dto.CommentSortDtoOut;
import ru.practicum.explore.comments.model.Comment;
import ru.practicum.explore.enums.ComState;
import ru.practicum.explore.event.EventService;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final JdbcTemplate jdbcTemplate;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentSortDtoOut addComment(Integer userId, Integer eventId, CommentDtoIn commentDtoIn) {
        userService.getUser(userId);
        eventService.getPublicEventById(eventId);
        Comment comment = commentMapper.mapCommentDtoInToComment(commentDtoIn);
        comment.setCreator(userId);
        comment.setStatus(ComState.PENDING);
        Comment newComment = commentRepository.save(comment);
        jdbcTemplate.update("INSERT INTO comments_events (comment_id, event_id) VALUES (?, ?)", newComment.getId(), eventId);
        return commentMapper.mapCommentToCommentShortDtoOut(comment);
    }

    @Transactional
    @Override
    public CommentSortDtoOut updateComment(Integer userId, Integer commentId, CommentDtoIn commentDtoIn) {
        userService.getUser(userId);
        Comment comment = findCommentWithCheck(commentId);
        checkCommentAndUserId(userId, comment);
        comment.setText(commentDtoIn.getText());
        comment.setStatus(ComState.PENDING);
        return commentMapper.mapCommentToCommentShortDtoOut(commentRepository.save(comment));
    }

    @Override
    public List<CommentSortDtoOut> getAllCommentsByUserId(Integer userId) {
        userService.getUser(userId);
        return commentRepository.findAllByCreator(userId).stream().map(commentMapper::mapCommentToCommentShortDtoOut).toList();

    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteComment(Integer userId, Integer commentId) {
        userService.getUser(userId);
        Comment comment = findCommentWithCheck(commentId);
        checkCommentAndUserId(userId, comment);
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }


    @Override
    public List<CommentDtoOut> getAdminCommentsByUserId(Integer userId) {
        userService.getUser(userId);
        return commentRepository.findAllByCreator(userId).stream().map(commentMapper::mapCommentToCommentDtoOut).toList();

    }

    @Override
    public List<CommentDtoOut> getAdminCommentsByEventId(Integer eventId) {
        eventService.getPublishEventById(eventId);
        List<Integer> commentIds = jdbcTemplate.query("SELECT ce.comment_id FROM comments_events AS ce WHERE ce.event_id = ?", (rs, rowNum) -> rs.getInt("comment_id"), eventId);
        return commentRepository.getCommentsWithIds(commentIds).stream().map(commentMapper::mapCommentToCommentDtoOut).toList();

    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteAdminComment(Integer commentId) {
        Comment comment = findCommentWithCheck(commentId);
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public CommentDtoOut conformationAdminComment(Integer commentId, Boolean accept) {
        Comment comment = findCommentWithCheck(commentId);
        if (comment.getStatus().equals(ComState.PENDING) && !accept) {
            comment.setStatus(ComState.REJECTED);
        } else if ((comment.getStatus().equals(ComState.PENDING) && accept)) {
            comment.setStatus(ComState.PUBLISHED);
        } else {
            throw new ConflictException("Подтверждать или отклонять комментарии можно только со статусом ожидания!");
        }
        return commentMapper.mapCommentToCommentDtoOut(comment);
    }

    @Override
    public List<CommentDtoOut> getAdminPendingCommentsByEventId(Integer eventId) {
        eventService.getPublishEventById(eventId);
        List<Integer> commentIds = jdbcTemplate.query("SELECT ce.comment_id FROM comments_events AS ce WHERE ce.event_id = ?", (rs, rowNum) -> rs.getInt("comment_id"), eventId);
        return commentRepository.getPendingCommentsWithIds(commentIds).stream().map(commentMapper::mapCommentToCommentDtoOut).toList();

    }

    public Comment findCommentWithCheck(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    public void checkCommentAndUserId(Integer userId, Comment comment) {
        if (!userId.equals(comment.getCreator())) {
            throw new ConflictException("У вас нет прав на изменение этого комментария!");
        }
    }

}
