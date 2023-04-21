package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Building findByName(String name);
}
