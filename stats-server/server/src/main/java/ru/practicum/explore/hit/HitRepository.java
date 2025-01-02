package ru.practicum.explore.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.dto.Hit;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("select count(distinct h.ip) h from Hit as h where h.timestamp > ?1 and h.timestamp < ?2 and h.uri = ?3")
    Integer searchUniqueHits(String start, String end, String uri);

    @Query("select count(h) from Hit as h where h.timestamp > ?1 and h.timestamp < ?2 and h.uri = ?3")
    Integer searchHits(String start, String end, String uri);

}
