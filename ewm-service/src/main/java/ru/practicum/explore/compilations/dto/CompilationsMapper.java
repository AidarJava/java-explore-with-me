package ru.practicum.explore.compilations.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.explore.compilations.model.Compilations;
import ru.practicum.explore.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationsMapper {
    private final EventService eventService;
    private final JdbcTemplate jdbcTemplate;

    public Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = new Compilations();
        if (compilationsDtoIn.getPinned() == null) {
            compilations.setPinned(false);
        } else {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        compilations.setTitle(compilationsDtoIn.getTitle());
        return compilations;
    }

    public CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations) {
        CompilationsDtoOut compilationsDtoOut = new CompilationsDtoOut();
        List<Integer> eventIds = jdbcTemplate.query("SELECT ce.event_id FROM compilations_events AS ce WHERE ce.compilation_id = ?", (rs, rowNum) -> rs.getInt("event_id"), compilations.getId());
        compilationsDtoOut.setEvents(eventService.getCompilationsEvents(eventIds));
        compilationsDtoOut.setId(compilations.getId());
        compilationsDtoOut.setPinned(compilations.getPinned());
        compilationsDtoOut.setTitle(compilations.getTitle());
        return compilationsDtoOut;
    }
}
