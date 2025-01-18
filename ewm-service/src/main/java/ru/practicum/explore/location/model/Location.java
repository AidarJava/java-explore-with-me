package ru.practicum.explore.location.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Location {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
