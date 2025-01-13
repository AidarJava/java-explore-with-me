package ru.practicum.explore.compilations;

import org.springframework.http.ResponseEntity;
import ru.practicum.explore.compilations.dto.CompilationsDtoIn;
import ru.practicum.explore.compilations.dto.CompilationsDtoOut;

import java.util.List;

public interface CompilationsService {
    CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn);

    ResponseEntity<Void> deleteCompilation(Integer compId);

    CompilationsDtoOut updateCompilation(Integer compId, CompilationsDtoIn compilationsDtoIn);

    List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size);

    CompilationsDtoOut getPublicCompilationsById(Integer compId);
}
