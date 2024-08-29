package com.CMS.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CMS.Model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

}
