package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GasRepository extends JpaRepository<Gas, Long> {
//    List<Gas> findByBuilding(Building building);
    List<Gas> findByBuildingOrderByRecordedAtAsc(Building building);
}
