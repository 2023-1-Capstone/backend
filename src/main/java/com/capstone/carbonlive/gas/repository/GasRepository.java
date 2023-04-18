package com.capstone.carbonlive.gas.repository;

import com.capstone.carbonlive.building.Building;
import com.capstone.carbonlive.gas.Gas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GasRepository extends JpaRepository<Gas, Long> {

    List<Gas> findByBuilding(Building building);
}
