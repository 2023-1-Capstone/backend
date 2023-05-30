package com.capstone.carbonlive.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordRequest {
    private String username;
    private String name;
}
