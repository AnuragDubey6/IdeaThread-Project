package com.CMS.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CMS.Model.Category;
import com.CMS.Repo.CategoryRepository;

@Service
public class CategoryServices {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	
	public Category getCategoryById(Long categoryId)
	{
	    return categoryRepository.findById(categoryId).orElse(null); 
	}
	
	public Category createCategory(Category category)
	{
		return categoryRepository.save(category);
	}
	
	public void deleteCategory(Long categoryId) 
	{
        categoryRepository.deleteById(categoryId);
    }
	
	
	public List<Category> getAllCategories() 
	{
        return categoryRepository.findAll();
    }
	
	public Category getCategoryByName(String name) 
	{
        return categoryRepository.findByName(name);
    }
}

