package ru.practicum.explore.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class UserDtoOut {
    String email;
    Integer id;
    String name;
}
