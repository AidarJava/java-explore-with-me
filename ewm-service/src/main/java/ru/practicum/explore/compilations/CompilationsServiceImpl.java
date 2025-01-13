package ru.practicum.explore.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.explore.compilations.dto.CompilationsDtoIn;
import ru.practicum.explore.compilations.dto.CompilationsDtoOut;
import ru.practicum.explore.compilations.dto.CompilationsMapper;
import ru.practicum.explore.compilations.model.Compilations;
import ru.practicum.explore.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.save(compilationsMapper.mapCompilationsDtoInToCompilations(compilationsDtoIn));
        for (int i = 0; i < compilationsDtoIn.getEvents().size(); i++) {
            jdbcTemplate.update("INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)", compilations.getId(), compilationsDtoIn.getEvents().get(i));
        }
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations);
    }

    @Override
    public ResponseEntity<Void> deleteCompilation(Integer compId) {
        Compilations compilations = compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationsRepository.delete(compilations);
        return ResponseEntity.noContent().build();
    }

    @Override
    public CompilationsDtoOut updateCompilation(Integer compId, CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        if (compilationsDtoIn.getEvents() != null) {
            jdbcTemplate.update("DELETE FROM compilations_events AS ce WHERE ce.compilation_id = ?", compilations.getId());
            for (int i = 0; i < compilationsDtoIn.getEvents().size(); i++) {
                jdbcTemplate.update("INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)", compilations.getId(), compilationsDtoIn.getEvents().get(i));
            }
        }
        if (compilationsDtoIn.getPinned() != null) {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        if (compilationsDtoIn.getTitle() != null) {
            compilations.setTitle(compilationsDtoIn.getTitle());
        }
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilationsRepository.save(compilations));
    }

    @Override
    public List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned != null) {
            return compilationsRepository.getPublicCompByPinned(pinned, from, size).stream().map(compilationsMapper::mapCompilationsToCompilationsDtoOut).toList();
        } else {
            return compilationsRepository.getPublicComp(from, size).stream().map(compilationsMapper::mapCompilationsToCompilationsDtoOut).toList();
        }
    }

    @Override
    public CompilationsDtoOut getPublicCompilationsById(Integer compId) {
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilationsRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }
}
