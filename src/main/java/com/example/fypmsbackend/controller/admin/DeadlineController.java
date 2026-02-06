package com.example.fypmsbackend.controller.admin;

import com.example.fypmsbackend.model.Deadline;
import com.example.fypmsbackend.repository.DeadlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/deadlines")
@RequiredArgsConstructor
public class DeadlineController {

    private final DeadlineRepository repo;

    @PostMapping
    public Deadline save(@RequestBody Deadline d) {
        d.setCreatedAt(LocalDateTime.now());
        return repo.save(d);
    }

    @GetMapping
    public List<Deadline> all() {
        return repo.findAll();
    }
}
