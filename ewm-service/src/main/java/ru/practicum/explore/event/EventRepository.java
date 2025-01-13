package ru.practicum.explore.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(value = "SELECT * FROM events AS e WHERE e.initiator :userId OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getEvents(@Param("userId") Integer userId, @Param("from") Integer from, @Param("size") Integer size);

    Optional<Event> findByIdAndInitiator(Integer eventId, Integer userId);

    @Query(value = "SELECT * FROM events AS e WHERE (e.annotation LIKE CONCAT('%', :lowText, '%') OR e.description LIKE CONCAT('%', :lowText, '%')) AND e.paid = :paid " +
            "AND e.event_date > :rangeStart AND e.event_date < :rangeEnd AND e.state = 'PUBLISHED'", nativeQuery = true)
    List<Event> getPublicEventByTextAndPaidAndStartAndEnd(@Param("lowText") String lowText, @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd);

    @Query(value = "SELECT * FROM events AS e WHERE (e.annotation LIKE CONCAT('%', :lowText, '%') OR e.description LIKE CONCAT('%', :lowText, '%')) AND e.paid = :paid " +
            "AND e.event_date > :rangeStart AND e.state = 'PUBLISHED'", nativeQuery = true)
    List<Event> getPublicEventByTextAndPaidAndStart(@Param("lowText") String lowText, @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart);

    @Query(value = "SELECT * FROM events AS e WHERE (e.annotation LIKE CONCAT('%', :lowText, '%') OR e.description LIKE CONCAT('%', :lowText, '%')) AND e.paid = :paid " +
            "AND e.event_date < :rangeEnd AND e.state = 'PUBLISHED'", nativeQuery = true)
    List<Event> getPublicEventByTextAndPaidAndEnd(@Param("lowText") String lowText, @Param("paid") Boolean paid, @Param("rangeEnd") LocalDateTime rangeEnd);

    @Query("SELECT e FROM Event e WHERE (e.annotation LIKE %?1% OR e.description LIKE %?1%) AND e.paid = ?2 AND e.state = 'PUBLISHED'")
    List<Event> getPublicEventByTextAndPaid(String lowText, Boolean paid);


    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids AND e.category IN :category OFFSET :from LIMIT :size ORDER BY e.event_date ASC", nativeQuery = true)
    List<Event> getEventsSortDateAndCategory(@Param("ids") List<Integer> ids, @Param("category") Integer[] category, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids AND e.category IN :category OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getEventsSortViewsAndCategory(@Param("ids") List<Integer> ids, @Param("category") Integer[] category, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids OFFSET :from LIMIT :size ORDER BY e.event_date ASC", nativeQuery = true)
    List<Event> getEventsSortDate(@Param("ids") List<Integer> ids, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getEventsSortViews(@Param("ids") List<Integer> ids, @Param("from") Integer from, @Param("size") Integer size);


    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.event_date < :rangeEnd OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByStartAndEnd(@Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByStart(@Param("rangeStart") LocalDateTime rangeStart, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date < :rangeEnd OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventByEnd(@Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEvent(@Param("from") Integer from, @Param("size") Integer size);


    @Query(value = "SELECT * FROM events AS e WHERE e.initiator IN :ids AND e.category IN :category", nativeQuery = true)
    List<Event> getAdminEventsByIdsAndCategory(@Param("ids") List<Integer> ids, @Param("category") Integer[] category);

    @Query(value = "SELECT * FROM events AS e WHERE e.initiator IN :ids", nativeQuery = true)
    List<Event> getAdminEventsByIds(@Param("ids") List<Integer> ids);

    @Query(value = "SELECT * FROM events AS e WHERE e.category IN :category ", nativeQuery = true)
    List<Event> getAdminEventsByCategory(@Param("category") Integer[] category);

    @Query(value = "SELECT * FROM events AS e WHERE e.state IN :states", nativeQuery = true)
    List<Event> getAdminEventsInStates(@Param("states") List<String> states);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids AND e.state IN :states", nativeQuery = true)
    List<Event> getAdminEventsInIdsAndStates(@Param("ids") List<Integer> ids, @Param("states") List<String> states);


    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.event_date < :rangeEnd AND e.id IN :ids OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByStartAndEnd(@Param("ids") List<Integer> ids, @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date > :rangeStart AND e.id IN :ids OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByStart(@Param("ids") List<Integer> ids, @Param("rangeStart") LocalDateTime rangeStart, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.event_date < :rangeEnd AND e.id IN :ids OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIdsByEnd(@Param("ids") List<Integer> ids, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM events AS e WHERE e.id IN :ids OFFSET :from LIMIT :size", nativeQuery = true)
    List<Event> getAdminEventInIds(@Param("ids") List<Integer> ids, @Param("from") Integer from, @Param("size") Integer size);

    @Query("select e from Event as e where e.id IN ?1")
    List<Event> getCompilationsEvents(List<Integer> ids);

}
