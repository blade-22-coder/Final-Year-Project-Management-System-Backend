package com.example.fypmsbackend.repository;

import com.example.fypmsbackend.model.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepository extends JpaRepository<Snapshot,Long> {

    List<Snapshot> findAllByStudentProfileId(Long studentId);

    List<Snapshot> findByStudentProfileId(Long studentId);
}
