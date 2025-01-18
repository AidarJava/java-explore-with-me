package ru.practicum.explore.participation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParticipationUpdateDtoOut {
    List<ParticipationDtoOut> confirmedRequests;
    List<ParticipationDtoOut> rejectedRequests;
}
