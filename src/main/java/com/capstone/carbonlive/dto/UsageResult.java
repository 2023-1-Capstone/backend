package com.capstone.carbonlive.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsageResult<T> {
    private List<T> result;

    public UsageResult(List<T> result) {
        this.result = result;
    }
}