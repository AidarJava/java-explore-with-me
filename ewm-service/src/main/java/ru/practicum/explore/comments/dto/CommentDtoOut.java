package ru.practicum.explore.comments.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.explore.event.dto.EventShortDtoOut;
import ru.practicum.explore.user.dto.UserShortDtoOut;

@Getter
@Setter
@ToString
public class CommentDtoOut {
    Integer id;
    EventShortDtoOut eventShortDtoOut;
    String text;
    UserShortDtoOut userShortDtoOut;
}
