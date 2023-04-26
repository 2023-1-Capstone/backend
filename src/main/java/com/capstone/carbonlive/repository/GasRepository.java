package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GasRepository extends JpaRepository<Gas, Long> {
    List<Gas> findByBuildingOrderByRecordedAtAsc(Building building);
    List<Gas> findAll(Sort sort);
}
