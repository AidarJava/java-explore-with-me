package ru.practicum.explore.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByCreator(Integer userId);

    @Query("select c from Comment as c where c.id IN ?1")
    List<Comment> getCommentsWithIds(List<Integer> eventIds);
}
