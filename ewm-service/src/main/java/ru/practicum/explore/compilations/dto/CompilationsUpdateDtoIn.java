package ru.practicum.explore.compilations.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
public class CompilationsUpdateDtoIn {
    List<Integer> events;
    Boolean pinned;
    @Length(max = 50, min = 0)
    String title;
}
