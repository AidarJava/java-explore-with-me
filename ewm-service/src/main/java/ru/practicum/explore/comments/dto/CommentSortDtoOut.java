package ru.practicum.explore.comments.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentSortDtoOut {
    Integer id;
    String text;
    String eventAnnotation;
    String creator;
    String status;
}
