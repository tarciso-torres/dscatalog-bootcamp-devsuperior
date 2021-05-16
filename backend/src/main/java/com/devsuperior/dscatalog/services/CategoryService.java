package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		return categoryRepository
				.findAll()
				.stream()
				.map(category -> new CategoryDTO(category))
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		return new CategoryDTO(categoryRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Entity not found!")));
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = categoryRepository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {			
			Category entity = categoryRepository.getOne(id);
			entity.setName(dto.getName());
			entity = categoryRepository.save(entity);
			return new CategoryDTO(entity);
			
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(String.format("Id not found: %l", id));
		}
	}

}
