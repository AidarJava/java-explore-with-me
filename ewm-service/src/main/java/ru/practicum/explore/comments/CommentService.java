package ru.practicum.explore.comments;

import org.springframework.http.ResponseEntity;
import ru.practicum.explore.comments.dto.CommentDtoIn;
import ru.practicum.explore.comments.dto.CommentDtoOut;
import ru.practicum.explore.comments.dto.CommentSortDtoOut;

import java.util.List;

public interface CommentService {
    CommentSortDtoOut addComment(Integer userId, Integer eventId, CommentDtoIn commentDtoIn);

    CommentSortDtoOut updateComment(Integer userId, Integer commentId, CommentDtoIn commentDtoIn);

    List<CommentSortDtoOut> getAllCommentsByUserId(Integer userId);

    List<CommentDtoOut> getAdminCommentsByUserId(Integer userId);

    ResponseEntity<Void> deleteComment(Integer userId, Integer commentId);

    List<CommentDtoOut> getAdminCommentsByEventId(Integer eventId);

    ResponseEntity<Void> deleteAdminComment(Integer commentId);

    CommentDtoOut conformationAdminComment(Integer commentId, Boolean accept);

    List<CommentDtoOut> getAdminPendingCommentsByEventId(Integer eventId);

}
