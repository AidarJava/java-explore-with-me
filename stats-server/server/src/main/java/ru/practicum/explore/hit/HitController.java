package ru.practicum.explore.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Stats;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitServiceImpl hitService;

    @PostMapping("/hit")
    public HitDtoOut addRecord(@RequestBody HitDtoIn hitDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addRecord, hitDtoIn - {}", hitDtoIn);
        return hitService.addRecord(hitDtoIn);
    }

    @GetMapping("/stats")
    public List<Stats> getStats(@RequestParam(name = "start") String start,
                                @RequestParam(name = "end") String end,
                                @RequestParam(name = "uris") String[] uris,
                                @RequestParam(name = "unique") Boolean unique) {
        log.info("GET/ Проверка параметров запроса метода getStats, start - {}, end - {}, uris - {}, unique - {}", start, end, uris, unique);
        return hitService.getStats(start, end, uris, unique);
    }

}
