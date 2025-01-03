package ru.practicum.explore.hit;

import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Stats;

import java.util.List;

public interface HitService {
    HitDtoOut addRecord(HitDtoIn hitDtoIn);

    List<Stats> getStats(String start, String end, String[] uris, Boolean unique);
}
