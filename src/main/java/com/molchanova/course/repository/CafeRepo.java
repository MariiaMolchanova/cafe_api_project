package com.molchanova.course.repository;


import com.molchanova.course.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeRepo extends JpaRepository<Cafe, Long> {
}
