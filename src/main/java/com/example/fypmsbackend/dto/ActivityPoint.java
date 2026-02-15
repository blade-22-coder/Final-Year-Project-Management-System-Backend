package com.example.fypmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityPoint {
    private String date;
    private long count;
}
