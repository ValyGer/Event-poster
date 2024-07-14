package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoRequest;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.errors.ConflictException;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.dto.EventAdminParams;
import ru.practicum.ewm.event.service.EventService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventService eventService;

    // Часть admin

    public CategoryDto createCategory(CategoryDtoRequest categoryDtoRequest) {
        log.info("Категория {} успешно добавлен", categoryDtoRequest);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDtoRequest)));
    }

    @Transactional
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " was not found"));
        categoryRepository.deleteById(catId);
        log.info("Категория с id = {}, успешно удалена", catId);
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDtoRequest categoryDtoRequest) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id = " + catId + " was not found");
        }
        log.info("Категория с id = {} успешно обновлена", catId);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDtoRequest)));
    }

    // Часть public

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        List<Category> categories;
        categories = categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();
        log.info("Список категорий успешно выдан");
        return categories.stream().map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("Поиск категории с id = {}", catId);
        return categoryMapper.toCategoryDto(getCategoryByIdNotMapping(catId));
    }

    public Category getCategoryByIdNotMapping(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id = " + catId + " was not found"));
    }
}
