package com.thinture.tracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPushPayload {

    @JsonProperty("ref")
    private String ref; // e.g. "refs/heads/main"

    @JsonProperty("pusher")
    private Pusher pusher;

    @JsonProperty("repository")
    private Repository repository;

    @JsonProperty("commits")
    private List<Commit> commits;

    @JsonProperty("head_commit")
    private Commit headCommit;

    // ── Inner classes ────────────────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pusher {
        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        @JsonProperty("name")
        private String name;

        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("html_url")
        private String htmlUrl;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        @JsonProperty("id")
        private String id;

        @JsonProperty("message")
        private String message;

        @JsonProperty("url")
        private String url;

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("author")
        private CommitAuthor author;

        @JsonProperty("added")
        private List<String> added;

        @JsonProperty("modified")
        private List<String> modified;

        @JsonProperty("removed")
        private List<String> removed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitAuthor {
        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("username")
        private String username;
    }
}
