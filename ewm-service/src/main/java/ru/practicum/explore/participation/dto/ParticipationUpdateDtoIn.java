package ru.practicum.explore.participation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParticipationUpdateDtoIn {
    List<Integer> requestIds;
    String status;
}
