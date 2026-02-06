package com.example.fypmsbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationRequest {
    private Long studentProfileId;
    private Long supervisorId;
}
