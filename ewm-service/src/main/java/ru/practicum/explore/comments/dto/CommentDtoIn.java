package ru.practicum.explore.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDtoIn {
    @NotBlank
    String text;
}
