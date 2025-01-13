package ru.practicum.explore.categories.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.categories.CategoryService;
import ru.practicum.explore.categories.dto.CategoryDtoIn;
import ru.practicum.explore.categories.dto.CategoryDtoOut;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDtoOut addCategory(@Valid @RequestBody CategoryDtoIn categoryDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addCategory, categoryDtoIn - {}", categoryDtoIn.getName());
        return categoryService.addCategory(categoryDtoIn);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "catId") Integer catId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteCategory, catId - {}", catId);
        return categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDtoOut updateCategory(@PathVariable(name = "catId") Integer catId,
                                         @Valid @RequestBody CategoryDtoIn categoryDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateCategory, catId - {}, categoryDtoIn - {}", catId, categoryDtoIn);
        return categoryService.updateCategory(catId, categoryDtoIn);
    }
}

