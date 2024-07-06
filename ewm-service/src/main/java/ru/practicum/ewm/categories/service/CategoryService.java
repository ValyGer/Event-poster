package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryDtoRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDtoRequest categoryDtoRequest);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDtoRequest categoryDtoRequest);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catsId);
}
