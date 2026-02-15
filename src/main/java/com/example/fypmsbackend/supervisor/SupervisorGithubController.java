package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.model.Comment;
import com.example.fypmsbackend.model.Github;
import com.example.fypmsbackend.repository.GithubRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor/github")
public class SupervisorGithubController {

    private final GithubRepository repo;
    public SupervisorGithubController(GithubRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{studentProfileId}")
    public Github getRepo(@PathVariable Long studentProfileId) {
        return repo.findByStudentProfileId(studentProfileId);
    }

    @PostMapping("/{id}/comment")
    public Github comment(@PathVariable Long id,
                          @RequestBody String comment) {
        Github g = repo.findById(id).orElseThrow();
        g.setSupervisorComment(comment);
        return repo.save(g);
    }
}
