package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryDtoRequest;
import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.categories.repository.CategoryRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(CategoryDtoRequest categoryDtoRequest) {
        log.info("Категория {} успешно добавлен", categoryDtoRequest);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDtoRequest)));
    }

    public void deleteCategory(Long catId) {
    }

    public CategoryDto updateCategory(Long catId, CategoryDtoRequest categoryDtoRequest) {
        return null;
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return null;
    }

    public CategoryDto getCategoryById(Long catsId, Integer from, Integer size) {
        return null;
    }
}
