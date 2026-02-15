package com.example.fypmsbackend.supervisor;

import com.example.fypmsbackend.model.Documentation;
import com.example.fypmsbackend.repository.DocumentationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/docs")
public class SupervisorDocumentationController {

    private final DocumentationRepository repo;

    public SupervisorDocumentationController(DocumentationRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{studentProfileId}")
    public List<Documentation> findByStudentProfileId(@PathVariable Long studentProfileId) {
        return repo.findByStudentProfileId(studentProfileId);
    }

    @PostMapping("/{docId}/comment")
    public Documentation comment(
            @PathVariable Long docId,
            @RequestBody String comment) {

        Documentation doc = repo.findById(docId).orElseThrow();
        doc.setSupervisorComment(comment);
        return repo.save(doc);

    }

}
