package com.thinture.tracker.service;

import com.thinture.tracker.dto.GitHubPushPayload;
import com.thinture.tracker.entity.CommitRecord;
import com.thinture.tracker.entity.PushAuthor;
import com.thinture.tracker.repository.CommitRecordRepository;
import com.thinture.tracker.repository.PushAuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubWebhookService {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookService.class);

    private final PushAuthorRepository authorRepository;
    private final CommitRecordRepository commitRepository;
    private final SlackNotificationService slackService;

    public GitHubWebhookService(PushAuthorRepository authorRepository,
                                 CommitRecordRepository commitRepository,
                                 SlackNotificationService slackService) {
        this.authorRepository = authorRepository;
        this.commitRepository = commitRepository;
        this.slackService = slackService;
    }

    @Transactional
    public void processPushEvent(GitHubPushPayload payload) {
        log.info("Processing push event from: {}", payload.getPusher().getName());

        // 1. Build and save PushAuthor
        PushAuthor author = buildAuthor(payload);
        PushAuthor savedAuthor = authorRepository.save(author);
        log.info("Saved PushAuthor with id: {}", savedAuthor.getId());

        // 2. Build and save CommitRecords linked to author
        List<CommitRecord> commits = buildCommits(payload, savedAuthor);
        commitRepository.saveAll(commits);
        log.info("Saved {} commits for author id: {}", commits.size(), savedAuthor.getId());

        // 3. Send Slack notification
        slackService.sendPushNotification(savedAuthor, commits);
    }

    private PushAuthor buildAuthor(GitHubPushPayload payload) {
        // Extract branch name from ref (e.g. "refs/heads/main" -> "main")
        String branch = payload.getRef() != null
                ? payload.getRef().replace("refs/heads/", "")
                : "unknown";

        String repoName = payload.getRepository() != null
                ? payload.getRepository().getFullName()
                : "unknown";

        GitHubPushPayload.Pusher pusher = payload.getPusher();

        return PushAuthor.builder()
                .name(pusher.getName())
                .email(pusher.getEmail() != null ? pusher.getEmail() : "")
                .username(pusher.getName())
                .repositoryName(repoName)
                .branch(branch)
                .pushedAt(LocalDateTime.now())
                .build();
    }

    private List<CommitRecord> buildCommits(GitHubPushPayload payload, PushAuthor author) {
        List<CommitRecord> records = new ArrayList<>();

        if (payload.getCommits() == null || payload.getCommits().isEmpty()) {
            log.warn("No commits found in push payload");
            return records;
        }

        for (GitHubPushPayload.Commit commit : payload.getCommits()) {
            CommitRecord record = CommitRecord.builder()
                    .commitId(commit.getId())
                    .message(commit.getMessage())
                    .url(commit.getUrl())
                    .timestamp(parseTimestamp(commit.getTimestamp()))
                    .addedFiles(commit.getAdded() != null ? String.join(", ", commit.getAdded()) : "")
                    .modifiedFiles(commit.getModified() != null ? String.join(", ", commit.getModified()) : "")
                    .removedFiles(commit.getRemoved() != null ? String.join(", ", commit.getRemoved()) : "")
                    .author(author)
                    .build();

            records.add(record);
        }

        return records;
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            if (timestamp != null && !timestamp.isEmpty()) {
                return OffsetDateTime.parse(timestamp).toLocalDateTime();
            }
        } catch (Exception e) {
            log.warn("Could not parse timestamp: {}", timestamp);
        }
        return LocalDateTime.now();
    }
}
