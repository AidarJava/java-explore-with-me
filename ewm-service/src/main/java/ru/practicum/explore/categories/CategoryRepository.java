package ru.practicum.explore.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.categories.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(value = "SELECT * FROM categories AS c OFFSET :from LIMIT :size", nativeQuery = true)
    List<Category> getCategories(@Param("from") Integer from, @Param("size") Integer size);

    Category findByName(String name);
}
