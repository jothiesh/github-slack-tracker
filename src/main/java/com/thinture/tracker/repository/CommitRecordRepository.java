package com.thinture.tracker.repository;

import com.thinture.tracker.entity.CommitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRecordRepository extends JpaRepository<CommitRecord, Long> {
    List<CommitRecord> findByAuthorId(Long authorId);
}
