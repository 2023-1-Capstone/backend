package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectricityRepository extends JpaRepository<Electricity, Long> {
    List<Electricity> findAllByBuilding(Building building);
    List<Electricity> findAllByBuilding(Building building, Sort sort);
}