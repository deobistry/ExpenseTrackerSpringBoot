package com.expensemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensemanager.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

}