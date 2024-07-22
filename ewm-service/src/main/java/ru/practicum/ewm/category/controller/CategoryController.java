package ru.practicum.ewm.category.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoRequest;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Часть admin

    @PostMapping(value = "/admin/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDtoRequest categoryDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDtoRequest));
    }

    @DeleteMapping(value = "/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@NonNull @PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(value = "/admin/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long catId,
                                                      @Valid @RequestBody CategoryDtoRequest categoryDtoRequest) {
        return ResponseEntity.ok().body(categoryService.updateCategory(catId, categoryDtoRequest));
    }

    // Часть public

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(catId));
    }
}
