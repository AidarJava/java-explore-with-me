package ru.practicum.explore.categories;

import org.springframework.http.ResponseEntity;
import ru.practicum.explore.categories.dto.CategoryDtoIn;
import ru.practicum.explore.categories.dto.CategoryDtoOut;

import java.util.List;

public interface CategoryService {
    List<CategoryDtoOut> getCategories(Integer from, Integer size);

    CategoryDtoOut getCategory(Integer catId);

    CategoryDtoOut addCategory(CategoryDtoIn categoryDtoIn);

    ResponseEntity<Void> deleteCategory(Integer catId);

    CategoryDtoOut updateCategory(Integer catId, CategoryDtoIn categoryDtoIn);
}
