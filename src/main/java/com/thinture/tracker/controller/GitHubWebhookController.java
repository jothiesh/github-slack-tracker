package com.thinture.tracker.controller;

import com.thinture.tracker.dto.GitHubPushPayload;
import com.thinture.tracker.service.GitHubWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class GitHubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);

    private final GitHubWebhookService webhookService;

    public GitHubWebhookController(GitHubWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * GitHub sends a POST to this endpoint on every push event.
     * Configure your GitHub repo webhook URL as:
     *   http://<your-ngrok-url>/webhook/github
     * Content type: application/json
     * Events: Just the push event
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubPush(
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType,
            @RequestBody GitHubPushPayload payload) {

        log.info("Received GitHub event: {}", eventType);

        if (!"push".equals(eventType)) {
            log.info("Ignoring non-push event: {}", eventType);
            return ResponseEntity.ok("Event ignored: " + eventType);
        }

        try {
            webhookService.processPushEvent(payload);
            return ResponseEntity.ok("Push event processed successfully");
        } catch (Exception e) {
            log.error("Error processing push event: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("GitHub Slack Tracker is running!");
    }
}
