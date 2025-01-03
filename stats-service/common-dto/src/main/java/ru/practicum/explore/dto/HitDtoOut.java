package ru.practicum.explore.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class HitDtoOut {
    int id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
