package ru.practicum.explore.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.categories.dto.CategoryDtoIn;
import ru.practicum.explore.categories.dto.CategoryDtoOut;
import ru.practicum.explore.categories.dto.CategoryMapper;
import ru.practicum.explore.categories.model.Category;
import ru.practicum.explore.event.EventRepository;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDtoOut> getCategories(Integer from, Integer size) {
        return categoryRepository.getCategories(from, size).stream().map(categoryMapper::mapCategoryToCategoryDtoOut).toList();
    }

    @Override
    public CategoryDtoOut getCategory(Integer catId) {
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found")));
    }

    @Transactional
    @Override
    public CategoryDtoOut addCategory(CategoryDtoIn categoryDtoIn) {
        validCat(categoryDtoIn.getName());
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.save(categoryMapper.mapCategoryDtoInToCategory(categoryDtoIn)));
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteCategory(Integer catId) {
        getCategory(catId);
        if (!eventRepository.findAllByCategory(catId).isEmpty()) {
            throw new ConflictException(("C категорий не должно быть связано ни одного события!"));
        }
        categoryRepository.deleteById(catId);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public CategoryDtoOut updateCategory(Integer catId, CategoryDtoIn categoryDtoIn) {
        Category categoryById = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        if (!categoryById.getName().equals(categoryDtoIn.getName())) {
            validCat(categoryDtoIn.getName());
        }
        categoryById.setName(categoryDtoIn.getName());
        return categoryMapper.mapCategoryToCategoryDtoOut(categoryRepository.save(categoryById));
    }

    public void validCat(String name) {
        if (categoryRepository.findByName(name) != null) {
            throw new ConflictException("Имя категории должно быть уникальным!");
        }
    }

}
