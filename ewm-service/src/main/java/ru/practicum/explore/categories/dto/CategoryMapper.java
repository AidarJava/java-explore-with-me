package ru.practicum.explore.categories.dto;

import org.springframework.stereotype.Component;
import ru.practicum.explore.categories.model.Category;

@Component
public class CategoryMapper {
    public CategoryDtoOut mapCategoryToCategoryDtoOut(Category category) {
        CategoryDtoOut categoryDtoOut = new CategoryDtoOut();
        categoryDtoOut.setId(category.getId());
        categoryDtoOut.setName(category.getName());
        return categoryDtoOut;
    }

    public Category mapCategoryDtoInToCategory(CategoryDtoIn categoryDtoIn) {
        Category category = new Category();
        category.setName(categoryDtoIn.getName());
        return category;
    }
}
