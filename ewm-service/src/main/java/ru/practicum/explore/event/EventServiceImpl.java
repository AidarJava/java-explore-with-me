package ru.practicum.explore.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.dto.EventDtoIn;
import ru.practicum.explore.event.dto.EventDtoOut;
import ru.practicum.explore.event.dto.EventMapper;
import ru.practicum.explore.event.dto.EventShortDtoOut;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.exception.BadRequestException;
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

    @Transactional
    @Override
    public EventDtoOut addEvent(Integer userId, EventDtoIn eventDtoIn) {
        Event event = eventMapper.mapEventDtoInToEvent(eventDtoIn);
        checkValidTime(event.getEventDate());
        event.setInitiator(userId);
        if (!eventDtoIn.getRequestModeration()) {
            event.setState("PUBLISHED");
            event.setPublishedOn(LocalDateTime.now());
        } else {
            event.setState("PENDING");
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getEvents(Integer userId, Integer from, Integer size) {
        return eventRepository.getEvents(userId, from, size).stream().map(eventMapper::mapEventToEventShortDtoOut)
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
    }

    @Override
    public EventDtoOut getFullEvent(Integer userId, Integer eventId) {
        return eventMapper.mapEventToEventDtoOut(eventRepository.findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found")));
    }

    @Transactional
    @Override
    public EventDtoOut updateEvent(Integer userId, Integer eventId, EventDtoIn eventDtoIn) {
        Event event = eventRepository.findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals("PUBLISHED")) {
            throw new BadRequestException("Event must not be published");
        } else if (!event.getState().equals("PENDING") && !event.getState().equals("CANCELED")) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }
        changeEventFields(event, eventDtoIn);
        if (eventDtoIn.getStateAction().equals("CANCEL_REVIEW")) {
            event.setState("PUBLISHED");
            event.setPublishedOn(LocalDateTime.now());
        } else if (eventDtoIn.getStateAction().equals("SEND_TO_REVIEW")) {
            event.setState("PENDING");
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getPublicEvent(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        String lowText = text.toLowerCase();
        lowText = lowText.replace("\"", "");
        List<Event> events;
        //сначала делаем выборку по датам, оплате и содержанию текста
        if (rangeStart != null && rangeEnd != null) {
            events = eventRepository.getPublicEventByTextAndPaidAndStartAndEnd(lowText, paid, parseDate(rangeStart), parseDate(rangeEnd));
        } else if (rangeStart != null && rangeEnd == null) {
            events = eventRepository.getPublicEventByTextAndPaidAndStart(lowText, paid, parseDate(rangeStart));
        } else if (rangeStart == null && rangeEnd != null) {
            events = eventRepository.getPublicEventByTextAndPaidAndEnd(lowText, paid, parseDate(rangeEnd));
        } else {
            events = eventRepository.getPublicEventByTextAndPaid(lowText, paid);
        }
        //фильтруем по лимиту запросов на участие
        List<Integer> ids;
        if (onlyAvailable) {
            List<EventDtoOut> nextEvents = events.stream().map(eventMapper::mapEventToEventDtoOut).toList();
            ids = nextEvents.stream().filter(eventDtoOut -> eventDtoOut.getConfirmedRequests() < eventDtoOut.getParticipantLimit())
                    .map(EventDtoOut::getId).toList();
        } else {
            ids = events.stream().map(Event::getId).toList();
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
        return eventMapper.mapEventToEventDtoOut(eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found")));
    }

    @Override
    public List<EventDtoOut> getAdminEvent(Integer[] users, String[] states, Integer[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) {

        List<Integer> ids = List.of();
        if (users != null) {
            ids = List.of(users);
        }
        //делаем выборку по списоку id категорий и id пользователей
        List<Integer> nextIds = List.of();
        if (categories != null & !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIdsAndCategory(ids, categories).stream().map(Event::getId).toList();
        } else if (categories != null & ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByCategory(categories).stream().map(Event::getId).toList();
        } else if (categories == null & !ids.isEmpty()) {
            nextIds = eventRepository.getAdminEventsByIds(ids).stream().map(Event::getId).toList();
        }

        if ((categories != null || !ids.isEmpty()) & nextIds.isEmpty()) { //ничего не нашли
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

        if (states != null & stepThreeIds.isEmpty()) { //ничего не нашли
            return new ArrayList<>();
        }

        //делаем выборку по датам
        List<Event> events;
        if (stepThreeIds.isEmpty()) {
            if (rangeStart != null & rangeEnd != null) {
                events = eventRepository.getAdminEventByStartAndEnd(parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null & rangeEnd == null) {
                events = eventRepository.getAdminEventByStart(parseDate(rangeStart), from, size);
            } else if (rangeStart == null & rangeEnd != null) {
                events = eventRepository.getAdminEventByEnd(parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEvent(from, size);
            }
        } else {
            if (rangeStart != null & rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByStartAndEnd(stepThreeIds, parseDate(rangeStart), parseDate(rangeEnd), from, size);
            } else if (rangeStart != null & rangeEnd == null) {
                events = eventRepository.getAdminEventInIdsByStart(stepThreeIds, parseDate(rangeStart), from, size);
            } else if (rangeStart == null & rangeEnd != null) {
                events = eventRepository.getAdminEventInIdsByEnd(stepThreeIds, parseDate(rangeEnd), from, size);
            } else {
                events = eventRepository.getAdminEventInIds(stepThreeIds, from, size);
            }
        }
        return events.stream().map(eventMapper::mapEventToEventDtoOut).toList();
    }


    @Transactional
    @Override
    public EventDtoOut updateAdminEvent(Integer eventId, EventDtoIn eventDtoIn) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        LocalDateTime date = parseDate(eventDtoIn.getEventDate());
        if (!date.isAfter(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        changeEventFields(event, eventDtoIn);
        if (eventDtoIn.getStateAction().equals("PUBLISH_EVENT")) {
            if (event.getState().equals("PENDING")) {
                event.setState("PUBLISHED");
                event.setPublishedOn(LocalDateTime.now());
            } else {
                throw new ForbiddenException("Cannot publish the event because it's not in the right state: PUBLISHED");
            }
        } else if (eventDtoIn.getStateAction().equals("REJECT_EVENT")) {
            if (!event.getState().equals("PUBLISHED")) {
                event.setState("CANCELED");
            } else {
                throw new ForbiddenException("Событие можно отклонить, только если оно еще не опубликовано");
            }
        }
        return eventMapper.mapEventToEventDtoOut(eventRepository.save(event));
    }

    @Override
    public List<EventShortDtoOut> getCompilationsEvents(List<Integer> eventIds) {
        return eventRepository.getCompilationsEvents(eventIds).stream().map(eventMapper::mapEventToEventShortDtoOut)
                .sorted(Comparator.comparing(EventShortDtoOut::getViews, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
    }

    public void checkValidTime(LocalDateTime time) {
        if (time.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента, EventDate - " + time);
        }
    }

    public LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date.replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void changeEventFields(Event event, EventDtoIn eventDtoIn) {
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
            checkValidTime(event.getEventDate());
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
