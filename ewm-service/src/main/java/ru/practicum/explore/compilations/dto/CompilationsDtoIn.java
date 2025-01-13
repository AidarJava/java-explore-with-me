package ru.practicum.explore.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
public class CompilationsDtoIn {
    List<Integer> events;
    Boolean pinned;
    @NotBlank
    @Length(max = 50, min = 0)
    String title;
}
