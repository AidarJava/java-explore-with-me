package ru.practicum.explore.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explore.enums.StateAction;
import ru.practicum.explore.location.model.Location;

@Getter
@Setter
@ToString
public class EventUpdateDtoIn {
    @Length(min = 20, max = 2000)
    String annotation;
    Integer category;
    @Length(min = 20, max = 7000)
    String description;
    String eventDate;
    @Valid
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
    @Length(min = 3, max = 120)
    String title;
}
