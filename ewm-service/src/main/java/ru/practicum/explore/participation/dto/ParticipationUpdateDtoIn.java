package ru.practicum.explore.participation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ParticipationUpdateDtoIn {
    List<Integer> requestIds;
    String status;
}
