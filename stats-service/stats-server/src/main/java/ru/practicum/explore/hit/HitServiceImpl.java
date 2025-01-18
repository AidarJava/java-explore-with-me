package ru.practicum.explore.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Stats;
import ru.practicum.explore.hit.exceptions.BadRequestException;
import ru.practicum.explore.mapper.HitMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Transactional
    public HitDtoOut addRecord(HitDtoIn hitDtoIn) {
        return hitMapper.mapRecordToRecordDtoOut(hitRepository.save(hitMapper.mapRecordDtoInToRecord(hitDtoIn)));
    }

    public List<Stats> getStats(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime cleanStart = LocalDateTime.parse(start.replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime cleanEnd = LocalDateTime.parse(end.replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (cleanEnd.isBefore(cleanStart)) {
            throw new BadRequestException("Дата начала позже, чем дата конца!");
        }
        List<Stats> stats = new ArrayList<>();
        if (uris != null) {
            List<String> cleanUris = Arrays.stream(uris).map(uri -> uri.replace("\"", "")).toList();
            if (unique) {
                return searchUniqueHits(cleanUris, cleanStart, cleanEnd, stats);
            } else {
                return searchHits(cleanUris, cleanStart, cleanEnd, stats);
            }
        } else {
            List<String> newUris = hitRepository.searchUniqueUris(cleanStart, cleanEnd);
            if (unique) {
                return searchUniqueHits(newUris, cleanStart, cleanEnd, stats);
            } else {
                return searchHits(newUris, cleanStart, cleanEnd, stats);
            }
        }
    }

    public List<Stats> searchUniqueHits(List<String> uris, LocalDateTime start, LocalDateTime end, List<Stats> stats) {
        uris.forEach(cleanUri -> {
            Integer count = hitRepository.searchUniqueHits(start, end, cleanUri);
            stats.add(new Stats("ewm-main-service", cleanUri, count));
        });
        stats.sort(Comparator.comparingInt(Stats::getHits).reversed());
        return stats;
    }

    public List<Stats> searchHits(List<String> uris, LocalDateTime start, LocalDateTime end, List<Stats> stats) {
        uris.forEach(cleanUri -> {
            Integer count = hitRepository.searchHits(start, end, cleanUri);
            stats.add(new Stats("ewm-main-service", cleanUri, count));
        });
        stats.sort(Comparator.comparingInt(Stats::getHits).reversed());
        return stats;
    }
}