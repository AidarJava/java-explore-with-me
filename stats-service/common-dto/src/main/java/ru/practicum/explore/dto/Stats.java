package ru.practicum.explore.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    String app;
    String uri;
    Integer hits;
}
