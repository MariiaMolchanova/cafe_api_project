package com.molchanova.course.repository;

import com.molchanova.course.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCafeId(Long cafeId);
}
