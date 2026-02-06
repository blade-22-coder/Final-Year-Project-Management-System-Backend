package com.example.fypmsbackend.service;

import com.example.fypmsbackend.model.Submission;
import org.springframework.stereotype.Service;

@Service
public class BatteryService {

    public int calculateBattery(Submission sub) {

        int approved = 0;
        int total = 5;

        if (sub.isTitleApproved()) approved++;
        if(sub.isProposalApproved()) approved++;
        if(sub.isFinalReportApproved()) approved++;
        if(sub.isGithubLinkApproved()) approved++;
        if(sub.isSnapshostsapproved()) approved++;

        return(approved * 100) / total;
    }
}
