package ru.practicum.explore.participation.dto;

import org.mapstruct.Mapper;
import ru.practicum.explore.participation.model.Participation;

@Mapper
public interface ParticipationMapper {
    ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation);
}
