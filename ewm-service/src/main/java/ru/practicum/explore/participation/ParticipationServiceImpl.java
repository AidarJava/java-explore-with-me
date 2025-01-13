package ru.practicum.explore.participation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.exception.BadRequestException;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.participation.dto.ParticipationDtoOut;
import ru.practicum.explore.participation.dto.ParticipationMapper;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoOut;
import ru.practicum.explore.participation.model.Participation;
import ru.practicum.explore.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    ParticipationMapper participationMapper = Mappers.getMapper(ParticipationMapper.class);

    @Override
    public List<ParticipationDtoOut> getUserParticipation(Integer userId, Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().equals(userId)) {
            throw new BadRequestException("Пользователь не является инициатором события");
        }
        return participationRepository.findAllByEvent(eventId).stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList();
    }

    @Transactional
    @Override
    public ParticipationUpdateDtoOut updateEventRequests(Integer userId, Integer eventId, ParticipationUpdateDtoIn participationUpdateDtoIn) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        Integer number = participationRepository.countByEvent(eventId);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= number) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие!");
        }
        if (participationRepository.countBadReq(participationUpdateDtoIn.getRequestIds()) > 0) {
            throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
        }
        ParticipationUpdateDtoOut participationUpdateDtoOut = new ParticipationUpdateDtoOut();
        List<Participation> list = new ArrayList<>();
        if (participationUpdateDtoIn.getStatus().equals("REJECTED")) { //если отмена
            list = participationRepository.participationReq(participationUpdateDtoIn.getRequestIds());
            List<Participation> rejectList = list;
            List<Participation> statusList = list;
            participationUpdateDtoOut.setConfirmedRequests(new ArrayList<>());
            participationUpdateDtoOut.setRejectedRequests(rejectList.stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList());
            statusList.forEach(part -> part.setStatus("REJECTED"));
            statusList.forEach(participationRepository::save);
        } else if (participationUpdateDtoIn.getStatus().equals("CONFIRMED")) {
            int freePlaces = event.getParticipantLimit() - number; //количесво свободных мест
            if (freePlaces >= participationUpdateDtoIn.getRequestIds().size()) { //свободных мест достаточно
                savePart(eventId, 0, participationUpdateDtoIn.getRequestIds().size(), list, "CONFIRMED", participationUpdateDtoIn, participationUpdateDtoOut);
            } else {
                int numberToApprove = participationUpdateDtoIn.getRequestIds().size() - freePlaces; //свободных мест не хватает
                savePart(eventId, 0, numberToApprove, list, "CONFIRMED", participationUpdateDtoIn, participationUpdateDtoOut);
                List<Participation> rejectList = new ArrayList<>(List.of());
                savePart(eventId, numberToApprove, participationUpdateDtoIn.getRequestIds().size(), rejectList, "REJECTED", participationUpdateDtoIn, participationUpdateDtoOut);
            }
        }
        return participationUpdateDtoOut;
    }

    @Override
    public List<ParticipationDtoOut> getParticipationForAnotherUser(Integer userId) {
        userService.getUser(userId);
        return participationRepository.findAllByRequester(userId).stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList();
    }

    @Transactional
    @Override
    public ParticipationDtoOut addParticipation(Integer userId, Integer eventId) {
        if (!participationRepository.findAllByRequesterAndEvent(userId, eventId).isEmpty()) {
            throw new ConflictException("Нельзя добавить повторный запрос!");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии!");
        }
        if (!event.getState().equals("PUBLISHED")) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }
        Integer number = participationRepository.countByEvent(eventId);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= number) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие!");
        }
        Participation participation = new Participation();
        participation.setEvent(eventId);
        participation.setRequester(userId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participation.setStatus("CONFIRMED");
        } else {
            participation.setStatus("PENDING");
        }
        return participationMapper.mapParticipationToParticipationDtoOut(participationRepository.save(participation));
    }

    @Transactional
    @Override
    public ParticipationDtoOut cancelParticipation(Integer userId, Integer requestId) {
        Participation participation = participationRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (!participation.getRequester().equals(userId)) {
            throw new ConflictException("Нельзя отменить чужой запрос на участие в событии!");
        }
        participation.setStatus("PENDING");
        return participationMapper.mapParticipationToParticipationDtoOut(participationRepository.save(participation));
    }

    public void savePart(Integer eventId, Integer start, Integer size, List<Participation> list, String status, ParticipationUpdateDtoIn participationUpdateDtoIn, ParticipationUpdateDtoOut participationUpdateDtoOut) {
        for (int i = start; i < size; i++) {
            Participation participation = participationRepository.findById(participationUpdateDtoIn.getRequestIds().get(i))
                    .orElseThrow(() -> new NotFoundException("Participation with id=" + eventId + " was not found"));
            list.add(participation);
        }
        participationUpdateDtoOut.setConfirmedRequests(list.stream().map(participationMapper::mapParticipationToParticipationDtoOut).toList());
        participationUpdateDtoOut.setRejectedRequests(new ArrayList<>());
        list.forEach(participation -> participation.setStatus(status));
        list.forEach(participationRepository::save);
    }

}
