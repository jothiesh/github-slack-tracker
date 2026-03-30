package com.thinture.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "push_author")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "pushed_at")
    private LocalDateTime pushedAt;

    // One author push can have many commits
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommitRecord> commits = new ArrayList<>();
}
