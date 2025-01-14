package ru.practicum.explore.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.controllers.EventClient;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.exception.BadRequestException;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.ForbiddenException;
import ru.practicum.explore.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventClient eventClient;

    @Transactional
    @Override
    public EventDtoOut addEvent(Integer userId, EventDtoIn eventDtoIn) {
        Event event = eventMapper.mapEventDtoInToEvent(eventDtoIn);
        checkValidTime(event.getEventDate(), 2, "Дата и время на которые намечено событие не может быть раньше, чем 2 часа от текущего момента");
        event.setInitiator(userId);
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getEvents(Integer userId, Integer from, Integer size) {
        return eventRepository.getEvents(userId, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut)
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
    }

    @Override
    public EventDtoOut getFullEvent(Integer userId, Integer eventId) {
        checkEvent(eventId);
        return eventMapper.mapEventToEventDtoOut(eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new BadRequestException("Это событие не добавлено выбранным пользователем!")));
    }

    @Transactional
    @Override
    public EventDtoOut updateEvent(Integer userId, Integer eventId, EventUpdateDtoIn eventDtoIn) {
        checkEvent(eventId);
        Event event = eventRepository.findByIdAndInitiator(eventId, userId).orElseThrow(() -> new BadRequestException("Это событие не добавлено выбранным пользователем!"));
        if (event.getState().equals("PUBLISHED")) {
            throw new BadRequestException("Event must not be published");
        } else if (!event.getState().equals("PENDING") && !event.getState().equals("CANCELED")) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }
        changeEventFields(event, eventDtoIn);
        if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals("CANCEL_REVIEW")) {
            event.setState("CANCELED");
        } else if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals("SEND_TO_REVIEW")) {
            event.setState("PENDING");
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getPublicEvent(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        eventClient.sendHit();
        String lowText = text.toLowerCase();
        lowText = lowText.replace("\"", "");
        List<Event> events;
        //сначала делаем выборку по датам и содержанию текста
        if (rangeStart != null && rangeEnd != null) {
            if (parseDate(rangeStart).isAfter(parseDate(rangeEnd))) {
                throw new BadRequestException("Дата начала события позже даты конца события!");
            }
            events = eventRepository.getPublicEventByTextAndStartAndEnd(lowText, parseDate(rangeStart), parseDate(rangeEnd));
        } else if (rangeStart != null && rangeEnd == null) {
            events = eventRepository.getPublicEventByTextAndStart(lowText, parseDate(rangeStart));
        } else if (rangeStart == null && rangeEnd != null) {
            events = eventRepository.getPublicEventByTextAndEnd(lowText, parseDate(rangeEnd));
        } else {
            events = eventRepository.getPublicEventByText(lowText);
        }
        //фильтруем по оплате
        List<Event> firstEvents;
        if (paid != null) {
            firstEvents = events.stream().filter(event -> event.getPaid().equals(paid)).toList();
        } else {
            firstEvents = events;
        }
        //фильтруем по лимиту запросов на участие
        List<Integer> ids;
        if (onlyAvailable) {
            List<EventDtoOut> nextEvents = firstEvents.stream().map(eventMapper::mapEventToEventDtoOut).toList();
            ids = nextEvents.stream().filter(eventDtoOut -> eventDtoOut.getConfirmedRequests() < eventDtoOut.getParticipantLimit())
                    .map(EventDtoOut::getId).toList();
        } else {
            ids = firstEvents.stream().map(Event::getId).toList();
        }
        //фильтруем по категориям
        if (categories != null) {
            //сразу делаем сортировку и выборку
            if (sort != null && sort.equals("EVENT_DATE")) {
                return eventRepository.getEventsSortDateAndCategory(ids, categories, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut).toList();
            } else {
                return eventRepository.getEventsSortViewsAndCategory(ids, categories, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut)
                        .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
            }
        } else {
            if (sort != null && sort.equals("EVENT_DATE")) {
                return eventRepository.getEventsSortDate(ids, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut).toList();
            } else {
                return eventRepository.getEventsSortViews(ids, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut)
                        .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
            }
        }
    }

    @Override
    public EventDtoOut getPublicEventById(Integer eventId) {
        Event event = eventRepository.getPublicEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals("PUBLISHED")) {
            eventClient.sendHitId(eventId);
        }
        return eventMapper.mapEventToEventDtoOut(event);
    }

    @Override
    public List<EventDtoOut> getAdminEvent(Integer[] users, String[] states, Integer[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<Integer> ids = List.of();
        if (users != null) {
            ids = List.of(users);
        }
        //делаем выборку по списоку id категорий и id пользователей
        List<Integer> nextIds = List.of();
        if (categories != null && !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIdsAndCategory(ids, categories).stream().map(Event::getId).toList();
        } else if (categories != null && ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByCategory(categories).stream().map(Event::getId).toList();
        } else if (categories == null && !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIds(ids).stream().map(Event::getId).toList();
        }

        if ((categories != null || !ids.isEmpty()) && nextIds.isEmpty()) { //ничего не нашли
            return new ArrayList<>();
        }

        //фильтруем по состояниям
        List<Integer> stepThreeIds = List.of();
        if (states != null) {
            List<String> goodStates = List.of(states);
            if (nextIds.isEmpty()) {
                stepThreeIds = eventRepository.getAdminEventsInStates(goodStates).stream().map(Event::getId).toList();
            } else {
                stepThreeIds = eventRepository.getAdminEventsInIdsAndStates(nextIds, goodStates).stream().map(Event::getId).toList();
            }
        } else {
            if (!nextIds.isEmpty()) {
                stepThreeIds = nextIds;
            }
        }

        if (states != null && stepThreeIds.isEmpty()) { //ничего не нашли
            return new ArrayList<>();
        }

        //делаем выборку по датам
        List<Event> events;
        if (stepThreeIds.isEmpty()) {
            if (rangeStart != null && rangeEnd != null) {
                events = eventRepository.getAdminEventByStartAndEnd(parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null && rangeEnd == null) {
                events = eventRepository.getAdminEventByStart(parseDate(rangeStart), from, size);
            } else if (rangeStart == null && rangeEnd != null) {
                events = eventRepository.getAdminEventByEnd(parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEvent(from, size);
            }
        } else {
            if (rangeStart != null && rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByStartAndEnd(stepThreeIds, parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null && rangeEnd == null) {
                events = eventRepository.getAdminEventInIdsByStart(stepThreeIds, parseDate(rangeStart), from, size);
            } else if (rangeStart == null && rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByEnd(stepThreeIds, parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEventInIds(stepThreeIds, from, size);
            }
        }
        return events.stream().map(eventMapper::mapEventToEventDtoOut).toList();
    }


    @Transactional
    @Override
    public EventDtoOut updateAdminEvent(Integer eventId, EventUpdateDtoIn eventDtoIn) {
        Event event = getEvent(eventId);
        if (event.getState().equals("PUBLISHED") || event.getState().equals("CANCELED")) {
            throw new ConflictException("Event must not be published or canceled");
        }
        LocalDateTime date = event.getEventDate();
        checkValidTime(date, 1, "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        changeEventFields(event, eventDtoIn);
        if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals("PUBLISH_EVENT")) {
            event.setState("PUBLISHED");
            event.setPublishedOn(LocalDateTime.now());
        } else if (eventDtoIn.getStateAction() != null && eventDtoIn.getStateAction().equals("REJECT_EVENT")) {
            event.setState("CANCELED");
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getCompilationsEvents(List<Integer> eventIds) {
        return eventRepository.getCompilationsEvents(eventIds).stream().map(eventMapper::mapEventToEventShortDtoOut)
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
    }

    public void checkValidTime(LocalDateTime time, Integer hours, String string) {
        if (LocalDateTime.now().plusHours(hours).isAfter(time)) {
            throw new BadRequestException(string);
        }
    }

    public void checkEvent(Integer eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public Event getEvent(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date.replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void changeEventFields(Event event, EventUpdateDtoIn eventDtoIn) {
        if (eventDtoIn.getAnnotation() != null) {
            event.setAnnotation(eventDtoIn.getAnnotation());
        }
        if (eventDtoIn.getCategory() != null) {
            event.setCategory(eventDtoIn.getCategory());
        }
        if (eventDtoIn.getDescription() != null) {
            event.setDescription(eventDtoIn.getDescription());
        }
        if (eventDtoIn.getEventDate() != null) {
            checkValidTime(event.getEventDate(), 2, "Дата и время на которые намечено событие не может быть раньше, чем 2 часа от текущего момента");
            checkValidTime(LocalDateTime.parse(eventDtoIn.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 0, "Дата и время события не может быть в прошлом");
            event.setEventDate(parseDate(eventDtoIn.getEventDate()));
        }
        if (eventDtoIn.getLocation() != null) {
            event.setLocationLat(eventDtoIn.getLocation().getLat());
            event.setLocationLon(eventDtoIn.getLocation().getLon());
        }
        if (eventDtoIn.getPaid() != null) {
            event.setPaid(eventDtoIn.getPaid());
        }
        if (eventDtoIn.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoIn.getParticipantLimit());
        }
        if (eventDtoIn.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoIn.getRequestModeration());
        }
        if (eventDtoIn.getTitle() != null) {
            event.setTitle(eventDtoIn.getTitle());
        }
    }
}
