package ru.practicum.explore.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Page<Category> findAll(Pageable pageable);

    Category findByName(String name);
}
