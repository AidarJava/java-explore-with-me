package ru.practicum.explore.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class CategoryDtoOut {
    Integer id;
    @NotBlank
    String name;
}
