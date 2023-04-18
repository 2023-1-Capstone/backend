package com.capstone.carbonlive.gas.service;

import com.capstone.carbonlive.gas.repository.GasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GasService {

    private final GasRepository gasRepository;
}
