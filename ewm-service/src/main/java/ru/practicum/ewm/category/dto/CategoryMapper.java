package ru.practicum.ewm.category.dto;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryDtoRequest categoryDtoRequest);

    CategoryDto toCategoryDto(Category category);
}
