package com.CMS.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CMS.Model.Category;
import com.CMS.Services.CategoryServices;

@RestController
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private CategoryServices categoryServices;
	

	
	@GetMapping("/{getAllCategorties}")
	public List<Category> getAllCategories()
	{
		return categoryServices.getAllCategories();
	}
	
	@GetMapping("/getCategorybyId/{categoryId}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId)
	{
		Category category= categoryServices.getCategoryById(categoryId);
		if(category !=null)
			return ResponseEntity.ok(category);
		else
			return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/getCategoryByname/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        Category category = categoryServices.getCategoryByName(name);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
	
	@PostMapping("/{createCategory}")
	public ResponseEntity<?> createCategory(@RequestBody Category categoryRequest) {
	    if (categoryRequest.getName() == null || categoryRequest.getName().isEmpty()) {
	        return ResponseEntity.badRequest().body("Category name must be provided");
	    }

	    Category category = new Category();
	    category.setName(categoryRequest.getName());

	    Category savedCategory = categoryServices.createCategory(category);
	    return ResponseEntity.ok(savedCategory);
	}

	
	@DeleteMapping("/{categoryID}")
	public ResponseEntity<String> deleteCategory (@PathVariable Long categoryId) {
        try {
            categoryServices.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with id " + categoryId);
        }
    }
}
