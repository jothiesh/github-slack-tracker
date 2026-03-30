# GitHub to Slack Commit Tracker
Spring Boot application that receives GitHub push webhooks, stores author + commits in H2, and notifies Slack.

---

## Tech Stack
- Java 17 + Spring Boot 3.2
- Spring Data JPA + H2 In-Memory DB
- Slack Incoming Webhooks API
- ngrok (for local webhook exposure)

---

## Project Structure
```
src/main/java/com/thinture/tracker/
├── GitHubSlackTrackerApplication.java   ← Main entry point
├── controller/
│   └── GitHubWebhookController.java     ← Receives GitHub POST events
├── service/
│   ├── GitHubWebhookService.java        ← Extracts + saves data
│   └── SlackNotificationService.java    ← Sends Slack message
├── entity/
│   ├── PushAuthor.java                  ← JPA Entity (who pushed)
│   └── CommitRecord.java                ← JPA Entity (what was pushed)
├── repository/
│   ├── PushAuthorRepository.java
│   └── CommitRecordRepository.java
├── dto/
│   └── GitHubPushPayload.java           ← Maps GitHub JSON payload
└── config/
    └── SecurityConfig.java              ← Allows H2 console + webhook
```

---

## Step 1 — Set Up Slack Incoming Webhook

1. Go to https://api.slack.com/apps → **Create New App** → **From Scratch**
2. Name it "GitHub Tracker", pick your workspace
3. Click **Incoming Webhooks** → Toggle ON
4. Click **Add New Webhook to Workspace** → Pick a channel → Allow
5. Copy the Webhook URL (looks like `https://hooks.slack.com/services/T.../B.../...`)
6. Paste it in `src/main/resources/application.properties`:
   ```
   slack.webhook.url=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
   ```

---

## Step 2 — Run the Application

```bash
mvn spring-boot:run
```

App starts at: http://localhost:8080
Health check: http://localhost:8080/webhook/health
H2 Console:   http://localhost:8080/h2-console

**H2 Console Login:**
- JDBC URL: `jdbc:h2:mem:trackerdb`
- Username: `sa`
- Password: (leave blank)

---

## Step 3 — Expose Local Port with ngrok

Download ngrok from https://ngrok.com/download, then:

```bash
ngrok http 8080
```

Copy the HTTPS forwarding URL, e.g.:
```
https://abc123.ngrok-free.app
```

Your webhook endpoint will be:
```
https://abc123.ngrok-free.app/webhook/github
```

---

## Step 4 — Configure GitHub Webhook

1. Go to your GitHub test repo → **Settings** → **Webhooks** → **Add webhook**
2. **Payload URL:** `https://abc123.ngrok-free.app/webhook/github`
3. **Content type:** `application/json`
4. **Which events:** Select **Just the push event**
5. Check **Active** → Click **Add webhook**

---

## Step 5 — Test It!

Push any code change to your GitHub repo:

```bash
echo "test" >> README.md
git add .
git commit -m "Test push for screening demo"
git push origin main
```

You should see:
- ✅ A Slack message in your channel
- ✅ Data in H2 console

---

## H2 SQL Queries for Demo

Run these in the H2 console to show data linkage:

```sql
-- Show all authors
SELECT * FROM PUSH_AUTHOR;

-- Show all commits
SELECT * FROM COMMIT_RECORD;

-- Show commits joined with author (proves the link)
SELECT
    pa.id AS author_id,
    pa.name AS author_name,
    pa.email,
    pa.repository_name,
    pa.branch,
    pa.pushed_at,
    cr.commit_id,
    cr.message AS commit_message
FROM PUSH_AUTHOR pa
JOIN COMMIT_RECORD cr ON cr.author_id = pa.id;
```

---

## Database Design

```
PUSH_AUTHOR                    COMMIT_RECORD
───────────────────────        ──────────────────────────────
id (PK)                   ←── author_id (FK)
name                           id (PK)
email                          commit_id
username                       message
repository_name                url
branch                         timestamp
pushed_at                      added_files
                               modified_files
                               removed_files
```

**Relationship:** One `PushAuthor` → Many `CommitRecord` (OneToMany / ManyToOne)

---

## Webhook Endpoint

```
POST /webhook/github
Header: X-GitHub-Event: push
Body: GitHub push payload (JSON)
```
