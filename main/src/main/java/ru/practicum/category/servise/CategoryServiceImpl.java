package ru.practicum.category.servise;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.utility.MyPageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.toCategory;
import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(toCategory(newCategoryDto));

        return toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Category", catId));

        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            category.setName(categoryDto.getName());
        }
        Category updateCategory = categoryRepository.save(category);

        return toCategoryDto(updateCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Category", catId));

        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        return categoryRepository.findAll(new MyPageRequest(from, size))
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Category", catId));

        return toCategoryDto(category);
    }
}
