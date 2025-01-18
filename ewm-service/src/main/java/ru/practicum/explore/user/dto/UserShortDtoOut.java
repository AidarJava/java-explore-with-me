package ru.practicum.explore.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class UserShortDtoOut {
    @NotBlank
    Integer id;
    @NotBlank
    String name;
}
