package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarbonRepository extends JpaRepository<Carbon, Long> {
    List<Carbon> findByBuildingOrderByRecordedAtAsc(Building building);
    List<Carbon> findByBuildingOrderByRecordedAtAscPredictionDesc(Building building);
}
