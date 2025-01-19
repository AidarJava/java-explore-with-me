package ru.practicum.explore.comments.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comments.CommentService;
import ru.practicum.explore.comments.dto.CommentDtoIn;
import ru.practicum.explore.comments.dto.CommentSortDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("comments/users/{userId}")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentSortDtoOut addComment(@PathVariable(name = "userId") Integer userId,
                                        @RequestParam(name = "eventId") Integer eventId,
                                        @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addComment, userId - {}, eventId - {}, commentDtoIn - {}", userId, eventId, commentDtoIn);
        return commentService.addComment(userId, eventId, commentDtoIn);
    }

    @PatchMapping
    public CommentSortDtoOut updateComment(@PathVariable(name = "userId") Integer userId,
                                           @RequestParam(name = "commentId") Integer commentId,
                                           @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода updateComment, userId - {}, commentId - {}, commentDtoIn - {}", userId, commentId, commentDtoIn);
        return commentService.updateComment(userId, commentId, commentDtoIn);
    }

    @GetMapping
    public List<CommentSortDtoOut> getAllCommentsByUserId(@PathVariable(name = "userId") Integer userId) {
        log.info("GET/ Проверка параметров запроса метода getAllCommentsByUserId, userId - {}", userId);
        return commentService.getAllCommentsByUserId(userId);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "userId") Integer userId,
                                              @RequestParam(name = "commentId") Integer commentId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteComment, userId - {}, commentId - {}", userId, commentId);
        return commentService.deleteComment(userId, commentId);
    }
}
