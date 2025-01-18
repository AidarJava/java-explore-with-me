package ru.practicum.explore.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findAllByRequesterAndEvent(Integer userId, Integer eventId);

    List<Participation> findAllByEvent(Integer eventId);

    List<Participation> findAllByRequester(Integer userId);

    Integer countByEvent(Integer eventId);

    @Query("select count(p) from Participation as p where (p.status = 'PUBLISHED' or p.status = 'CANCELED') and p.id IN ?1")
    Integer countBadReq(List<Integer> requestIds);

    @Query("select p from Participation as p where p.id IN ?1")
    List<Participation> participationReq(List<Integer> requestIds);

    @Query("select count(p) from Participation as p where p.event = ?1 and p.status = 'CONFIRMED'")
    Integer countByEventIdAndConfirmed(Integer eventId);
}
