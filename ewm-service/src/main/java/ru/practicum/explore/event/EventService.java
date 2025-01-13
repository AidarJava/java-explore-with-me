package ru.practicum.explore.event;

import ru.practicum.explore.event.dto.EventDtoIn;
import ru.practicum.explore.event.dto.EventDtoOut;
import ru.practicum.explore.event.dto.EventShortDtoOut;

import java.util.List;

public interface EventService {
    EventDtoOut addEvent(Integer userId, EventDtoIn eventDtoIn);

    List<EventShortDtoOut> getEvents(Integer userId, Integer from, Integer size);

    EventDtoOut getFullEvent(Integer userId, Integer eventId);

    EventDtoOut updateEvent(Integer userId, Integer eventId, EventDtoIn eventDtoIn);

    List<EventShortDtoOut> getPublicEvent(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventDtoOut getPublicEventById(Integer eventId);

    List<EventDtoOut> getAdminEvent(Integer[] users, String[] states, Integer[] categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventDtoOut updateAdminEvent(Integer eventId, EventDtoIn eventDtoIn);

    List<EventShortDtoOut> getCompilationsEvents(List<Integer> eventIds);
}