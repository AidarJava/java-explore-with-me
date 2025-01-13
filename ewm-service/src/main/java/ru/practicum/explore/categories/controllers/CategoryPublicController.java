package ru.practicum.explore.categories.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.categories.CategoryService;
import ru.practicum.explore.categories.dto.CategoryDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDtoOut> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET/ Проверка параметров запроса метода getCategories, from - {}, size - {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDtoOut getCategory(@PathVariable(name = "catId") Integer catId) {
        log.info("GET/ Проверка параметров запроса метода getCategory, catId - {}", catId);
        return categoryService.getCategory(catId);
    }
}
