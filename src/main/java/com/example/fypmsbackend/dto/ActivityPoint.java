package com.example.fypmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityPoint {
    private String label;
    private long value;
}
