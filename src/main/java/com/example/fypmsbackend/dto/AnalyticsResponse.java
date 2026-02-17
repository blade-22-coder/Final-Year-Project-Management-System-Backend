package com.example.fypmsbackend.dto;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalyticsResponse {

    private List<Integer> progress;
    private List<Integer> repoCommits;
}
