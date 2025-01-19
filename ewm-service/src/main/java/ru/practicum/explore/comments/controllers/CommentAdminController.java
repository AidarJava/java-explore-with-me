package ru.practicum.explore.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comments.CommentService;
import ru.practicum.explore.comments.dto.CommentDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("{userId}")
    public List<CommentDtoOut> getAdminCommentsByUserId(@PathVariable(name = "userId") Integer userId) {
        log.info("GET/ Проверка параметров запроса метода getAdminCommentsByUserId, userId - {},", userId);
        return commentService.getAdminCommentsByUserId(userId);
    }

    @GetMapping()
    public List<CommentDtoOut> getAdminCommentsByEventId(@RequestParam(name = "eventId") Integer eventId) {
        log.info("GET/ Проверка параметров запроса метода getAdminCommentsByEventId, eventId - {},", eventId);
        return commentService.getAdminCommentsByEventId(eventId);
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> deleteAdminComment(@PathVariable(name = "commentId") Integer commentId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteAdminComment, commentId - {},", commentId);
        return commentService.deleteAdminComment(commentId);
    }
}
