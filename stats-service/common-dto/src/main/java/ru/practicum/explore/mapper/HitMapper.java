package ru.practicum.explore.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;
import ru.practicum.explore.dto.Hit;

import java.time.format.DateTimeFormatter;

@Component
public class HitMapper {
    public HitDtoOut mapRecordToRecordDtoOut(Hit hit) {
        HitDtoOut hitDtoOut = new HitDtoOut();
        hitDtoOut.setId(hit.getId());
        hitDtoOut.setApp(hit.getApp());
        hitDtoOut.setUri(hit.getUri());
        hitDtoOut.setIp(hit.getIp());
        hitDtoOut.setTimestamp(hit.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return hitDtoOut;
    }

    public Hit mapRecordDtoInToRecord(HitDtoIn hitDtoIn) {
        Hit hit = new Hit();
        hit.setApp(hitDtoIn.getApp());
        hit.setUri(hitDtoIn.getUri());
        hit.setIp(hitDtoIn.getIp());
        return hit;
    }
}
