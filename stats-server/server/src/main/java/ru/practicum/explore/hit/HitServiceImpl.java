package ru.practicum.explore.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Stats;
import ru.practicum.explore.mapper.HitMapper;

import java.util.ArrayList;
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
        if (unique) {
            List<Stats> stats = new ArrayList<>();
            for (int i = 0; i < uris.length; i++) {
                String cleanUri = uris[i].replace("\"", "");
                String cleanStart = start.replace("\"", "");
                String cleanEnd = end.replace("\"", "");
                Integer count = hitRepository.searchUniqueHits(cleanStart, cleanEnd, cleanUri);
                stats.add(new Stats("ewm-main-service", cleanUri, count));
            }
            return stats;
        } else {
            List<Stats> stats = new ArrayList<>();
            for (int i = 0; i < uris.length; i++) {
                String cleanUri = uris[i].replace("\"", "");
                String cleanStart = start.replace("\"", "");
                String cleanEnd = end.replace("\"", "");
                Integer count = hitRepository.searchHits(cleanStart, cleanEnd, cleanUri);
                stats.add(new Stats("ewm-main-service", cleanUri, count));
            }
            return stats;
        }

    }
}