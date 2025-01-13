package ru.practicum.explore.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explore.categories.dto.CategoryDtoOut;
import ru.practicum.explore.user.dto.UserShortDtoOut;

@Slf4j
@Getter
@Setter
public class EventShortDtoOut {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDtoOut category;
    Integer confirmedRequests;
    @NotBlank
    String eventDate;
    Integer id;
    @NotNull
    UserShortDtoOut initiator;
    @NotNull
    Boolean paid;
    @NotBlank
    String title;
    Integer views;

}
