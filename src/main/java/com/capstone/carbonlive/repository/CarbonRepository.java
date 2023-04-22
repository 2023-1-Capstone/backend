package com.capstone.carbonlive.repository;

import com.capstone.carbonlive.entity.Carbon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonRepository extends JpaRepository<Carbon, Long> {
}
