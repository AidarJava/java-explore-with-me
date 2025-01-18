package ru.practicum.explore.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.explore.categories.dto.CategoryDtoOut;
import ru.practicum.explore.location.model.Location;
import ru.practicum.explore.user.dto.UserShortDtoOut;

@Getter
@Setter
public class EventDtoOut {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDtoOut category;
    Integer confirmedRequests;
    String createdOn;
    String description;
    @NotBlank
    String eventDate;
    Integer id;
    @NotNull
    UserShortDtoOut initiator;
    @NotNull
    Location location;
    @NotNull
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    @NotBlank
    String title;
    Integer views;
}
