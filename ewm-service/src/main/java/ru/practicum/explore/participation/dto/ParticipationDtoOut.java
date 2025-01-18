package ru.practicum.explore.participation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationDtoOut {
    String created;
    Integer event;
    Integer id;
    Integer requester;
    String status;
}
