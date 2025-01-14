package ru.practicum.explore.event.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore.categories.CategoryService;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.controllers.EventClient;
import ru.practicum.explore.location.model.Location;
import ru.practicum.explore.participation.ParticipationRepository;
import ru.practicum.explore.user.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryService categoryService;
    private final UserService userService;
    private final ParticipationRepository participationRepository;
    private final EventClient eventClient;

    public Event mapEventDtoInToEvent(EventDtoIn eventDtoIn) {
        Event event = new Event();
        event.setAnnotation(eventDtoIn.getAnnotation());
        event.setCategory(eventDtoIn.getCategory());
        event.setDescription(eventDtoIn.getDescription());
        event.setEventDate(LocalDateTime.parse(eventDtoIn.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setLocationLat(eventDtoIn.getLocation().getLat());
        event.setLocationLon(eventDtoIn.getLocation().getLon());
        if (eventDtoIn.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(eventDtoIn.getPaid());
        }
        if (eventDtoIn.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(eventDtoIn.getParticipantLimit());
        }

        if (eventDtoIn.getRequestModeration() != null && !eventDtoIn.getRequestModeration()) {
            event.setState("PUBLISHED");
            event.setPublishedOn(LocalDateTime.now());
            event.setRequestModeration(false);
        } else {
            event.setState("PENDING");
            event.setRequestModeration(true);
        }
        event.setTitle(eventDtoIn.getTitle());
        return event;
    }

    public EventDtoOut mapEventToEventDtoOut(Event event) {
        EventDtoOut eventDtoOut = new EventDtoOut();
        eventDtoOut.setAnnotation(event.getAnnotation());
        eventDtoOut.setCategory(categoryService.getCategory(event.getCategory()));
        eventDtoOut.setConfirmedRequests(participationRepository.countByEventIdAndConfirmed(event.getId()));
        eventDtoOut.setCreatedOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventDtoOut.setDescription(event.getDescription());
        eventDtoOut.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventDtoOut.setId(event.getId());
        eventDtoOut.setInitiator(userService.getUser(event.getInitiator()));
        Location location = new Location();
        location.setLat(event.getLocationLat());
        location.setLon(event.getLocationLon());
        eventDtoOut.setLocation(location);
        eventDtoOut.setPaid(event.getPaid());
        eventDtoOut.setParticipantLimit(event.getParticipantLimit());
        if (event.getPublishedOn() != null) {
            eventDtoOut.setPublishedOn(event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        eventDtoOut.setRequestModeration(event.getRequestModeration());
        eventDtoOut.setState(event.getState());
        eventDtoOut.setTitle(event.getTitle());
        String start = eventDtoOut.getEventDate();
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] uris = {"/events/" + event.getId()};
        eventDtoOut.setViews(eventClient.getHits(start, end, uris, false).getFirst().getHits());
        return eventDtoOut;
    }

    public EventShortDtoOut mapEventToEventShortDtoOut(Event event) {
        EventShortDtoOut eventShortDtoOut = new EventShortDtoOut();
        eventShortDtoOut.setAnnotation(event.getAnnotation());
        eventShortDtoOut.setCategory(categoryService.getCategory(event.getCategory()));
        eventShortDtoOut.setConfirmedRequests(participationRepository.countByEventIdAndConfirmed(event.getId()));
        eventShortDtoOut.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventShortDtoOut.setId(event.getId());
        eventShortDtoOut.setInitiator(userService.getUser(event.getInitiator()));
        eventShortDtoOut.setPaid(event.getPaid());
        eventShortDtoOut.setTitle(event.getTitle());
        String start = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] uris = {"/events/" + event.getId()};
        eventShortDtoOut.setViews(eventClient.getHits(start, end, uris, false).getFirst().getHits());
        return eventShortDtoOut;
    }

}

