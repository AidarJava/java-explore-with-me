package ru.practicum.explore.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.EventService;
import ru.practicum.explore.event.dto.EventDtoIn;
import ru.practicum.explore.event.dto.EventDtoOut;
import ru.practicum.explore.event.dto.EventShortDtoOut;
import ru.practicum.explore.participation.ParticipationService;
import ru.practicum.explore.participation.dto.ParticipationDtoOut;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoIn;
import ru.practicum.explore.participation.dto.ParticipationUpdateDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoOut addEvent(@PathVariable(name = "userId") Integer userId,
                                @Valid @RequestBody EventDtoIn eventDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addEvent, userId - {}, eventDtoIn - {}", userId, eventDtoIn);
        return eventService.addEvent(userId, eventDtoIn);
    }

    @GetMapping
    public List<EventShortDtoOut> getEvents(@PathVariable(name = "userId") Integer userId,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getEvent, userId - {}, from - {}, size - {}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDtoOut getFullEvent(@PathVariable(name = "userId") Integer userId,
                                    @PathVariable(name = "eventId") Integer eventId) {
        log.info("GET/ Проверка параметров запроса метода getFullEvent, userId - {}, eventId - {}", userId, eventId);
        return eventService.getFullEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoOut updateEvent(@PathVariable(name = "userId") Integer userId,
                                   @PathVariable(name = "eventId") Integer eventId,
                                   @RequestBody EventDtoIn eventDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateEvent, userId - {}, eventId - {}, eventDtoIn - {}", userId, eventId, eventDtoIn);
        return eventService.updateEvent(userId, eventId, eventDtoIn);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationDtoOut> getEventRequests(@PathVariable(name = "userId") Integer userId,
                                                      @PathVariable(name = "eventId") Integer eventId) {
        log.info("GET/ Проверка параметров запроса метода getEventRequests, userId - {}, eventId - {}", userId, eventId);
        return participationService.getUserParticipation(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationUpdateDtoOut updateEventRequests(@PathVariable(name = "userId") Integer userId,
                                                         @PathVariable(name = "eventId") Integer eventId,
                                                         @Valid @RequestBody ParticipationUpdateDtoIn participationUpdateDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateEventRequests, userId - {}, eventId - {}, participationUpdateDtoIn - {}", userId, eventId, participationUpdateDtoIn);
        return participationService.updateEventRequests(userId, eventId, participationUpdateDtoIn);
    }
}
