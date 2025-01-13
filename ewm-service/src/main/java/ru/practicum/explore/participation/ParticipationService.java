package ru.practicum.explore.participation;

import ru.practicum.explore.participation.dto.ParticipationDtoOut;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoOut;

import java.util.List;

public interface ParticipationService {
    List<ParticipationDtoOut> getUserParticipation(Integer userId, Integer eventId);

    ParticipationUpdateDtoOut updateEventRequests(Integer userId, Integer eventId, ParticipationUpdateDtoIn participationUpdateDtoIn);

    List<ParticipationDtoOut> getParticipationForAnotherUser(Integer userId);

    ParticipationDtoOut addParticipation(Integer userId, Integer eventId);

    ParticipationDtoOut cancelParticipation(Integer userId, Integer requestId);
}
