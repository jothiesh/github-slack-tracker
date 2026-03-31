package com.thinture.tracker.service;

import com.thinture.tracker.entity.CommitRecord;
import com.thinture.tracker.entity.PushAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SlackNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);

    private static final String SLACK_WEBHOOK_URL =
        "https://hooks.slack.com/services/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";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendPushNotification(PushAuthor author, List<CommitRecord> commits) {
        try {
            String message = buildSlackMessage(author, commits);
            sendToSlack(message);
            log.info("Slack notification sent for push by: {}", author.getName());
        } catch (Exception e) {
            log.error("Failed to send Slack notification: {}"
            		+ ""
            		+ ""
            		+ "", e.getMessage(), e);
        }
    }

    private String buildSlackMessage(PushAuthor author, List<CommitRecord> commits) {
        StringBuilder sb = new StringBuilder();
        sb.append(":rocket: *New Push to GitHub!*\n");
        sb.append("──────────────────────────\n");
        sb.append(String.format("*Author:* %s\n", author.getName()));
        sb.append(String.format("*Repository:* %s\n", author.getRepositoryName()));
        sb.append(String.format("*Branch:* %s\n", author.getBranch()));
        sb.append(String.format("*Total Commits:* %d\n", commits.size()));
        sb.append("──────────────────────────\n");
        sb.append("*Commits:*\n");
        for (CommitRecord commit : commits) {
            String shortId = commit.getCommitId().substring(0, Math.min(7, commit.getCommitId().length()));
            sb.append(String.format("  - %s: %s\n", shortId, commit.getMessage()));
        }
        return sb.toString();
    }

    private void sendToSlack(String message) {
        String jsonPayload = String.format("{\"text\": \"%s\"}",
                message.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                SLACK_WEBHOOK_URL, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Slack message delivered successfully");
        } else {
            log.warn("Slack responded with status: {}", response.getStatusCode());
        }
    }
}