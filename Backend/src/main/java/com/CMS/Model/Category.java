package com.CMS.Model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="categories")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long categoryId;
	
	@Column(name="cname", nullable=false, unique=true)
	private String name;
	
	@OneToMany(mappedBy = "category")
	@JsonManagedReference
	private List<Blog> blogs;
	
	public Category()
	{
		
	}

	public Category(int categoryId, String name, List<Blog> blogs) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.blogs = blogs;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Blog> getBlogs() {
		return blogs;
	}

	public void setBlogs(List<Blog> blogs) {
		this.blogs = blogs;
	}
	
}
