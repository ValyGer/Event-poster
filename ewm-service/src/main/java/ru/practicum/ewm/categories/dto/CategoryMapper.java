package ru.practicum.ewm.categories.dto;

import org.mapstruct.Mapper;
import ru.practicum.ewm.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryDtoRequest categoryDtoRequest);

    CategoryDto toCategoryDto(Category category);
}
