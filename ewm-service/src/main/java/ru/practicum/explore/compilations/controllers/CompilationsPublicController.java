package ru.practicum.explore.compilations.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilations.CompilationsService;
import ru.practicum.explore.compilations.dto.CompilationsDtoOut;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/compilations")
public class CompilationsPublicController {
    private final CompilationsService compilationsService;

    @GetMapping
    public List<CompilationsDtoOut> getPublicCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getPublicCompilations, pinned - {}, from - {}, size - {}", pinned, from, size);
        return compilationsService.getPublicCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationsDtoOut getPublicCompilationsById(@PathVariable(name = "compId") Integer compId) {
        log.info("GET/ Проверка параметров запроса метода getPublicCompilationsById, compId - {}", compId);
        return compilationsService.getPublicCompilationsById(compId);
    }

}
