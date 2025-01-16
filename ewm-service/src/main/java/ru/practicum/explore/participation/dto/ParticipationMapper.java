package ru.practicum.explore.participation.dto;


import org.springframework.stereotype.Component;
import ru.practicum.explore.participation.model.Participation;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class ParticipationMapper {
    public ParticipationDtoOut mapParticipationToParticipationDtoOut(Participation participation) {
        ParticipationDtoOut participationDtoOut = new ParticipationDtoOut();
        participationDtoOut.setCreated(participation.getCreated().truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")));
        participationDtoOut.setEvent(participation.getEvent());
        participationDtoOut.setId(participation.getId());
        participationDtoOut.setRequester(participation.getRequester());
        participationDtoOut.setStatus(participation.getStatus().toString());
        return participationDtoOut;
    }
}
