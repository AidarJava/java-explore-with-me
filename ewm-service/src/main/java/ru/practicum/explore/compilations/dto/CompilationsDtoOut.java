package ru.practicum.explore.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explore.event.dto.EventShortDtoOut;

import java.util.List;

@Getter
@Setter
@ToString
public class CompilationsDtoOut {
    List<EventShortDtoOut> events;
    @NotNull
    Integer id;
    @NotNull
    Boolean pinned;
    @NotBlank
    @Length(max = 50, min = 0)
    String title;
}
