package com.example.fypmsbackend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AllocationRequest {
    private Long studentProfileId;
    private Long supervisorProfileId;
}
