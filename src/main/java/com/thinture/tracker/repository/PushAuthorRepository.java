package com.thinture.tracker.repository;

import com.thinture.tracker.entity.PushAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushAuthorRepository extends JpaRepository<PushAuthor, Long> {
}
