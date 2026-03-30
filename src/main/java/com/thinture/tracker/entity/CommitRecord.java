package com.thinture.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commit_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commit_id", nullable = false)
    private String commitId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "url")
    private String url;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "added_files", columnDefinition = "TEXT")
    private String addedFiles;

    @Column(name = "modified_files", columnDefinition = "TEXT")
    private String modifiedFiles;

    @Column(name = "removed_files", columnDefinition = "TEXT")
    private String removedFiles;

    // Many commits belong to one push author
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private PushAuthor author;
}
