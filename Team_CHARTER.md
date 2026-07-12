### Branch Types & Naming Conventions

1. **`main` (Production Branch)**
  * Contains production-ready code.
  * Direct commits are strictly **forbidden**.
  * Protected branch: Requires approval from maintainers and passing CI/CD pipelines.

2. **`develop` (Integration Branch)**
  * Primary development branch containing the latest delivered features for the next release.
  * Merges into `main` during release cycles.

3. **`feature/<short-description>`**
  * Cut from: `develop`
  * Merges back into: `develop`
  * Purpose: Developing new functionality or user stories.
  * *Example:* `feature/user-authentication`, `feature/cart-payment-gateway`

4. **`fix/<short-description>` / `bugfix/<short-description>`**
  * Cut from: `develop`
  * Merges back into: `develop`
  * Purpose: Resolving non-critical bugs found during development/QA.
  * *Example:* `fix/null-pointer-login`, `fix/profile-image-upload`

5. **`hotfix/<short-description>`**
  * Cut from: `main`
  * Merges back into: `main` AND `develop`
  * Purpose: Emergency patches for production issues.
  * *Example:* `hotfix/security-patch-v1.0.1`

---

## 3. Commit Message Prefix Rules

To maintain a clean, readable, and parseable git history, all commit messages **MUST** follow strict prefix rules adhering to the **Conventional Commits** specification.

### Commit Format Structure
```text
<type>(<scope>): <short summary in imperative mood>

[optional body]
[optional footer(s)]

## 3. Communication & SLAs (Abdelrahman Hayel Shat)

### 3.1 Internal Communications Protocols

- **Daily & Quick Communication:** Via the team's WhatsApp group.
    - The **"🚨 Blocker"** tag is used for any obstacle halting progress, and must be responded to within a maximum of **8 hours** during working hours.
- **Task Management & Tracking:**
    - **Individual Tasks:** The task is received via the app, discussed during the stand-up to confirm understanding, and delivered to the personal repository.
    - **Team Tasks:** The task is received via the app, followed by a dedicated meeting (Google Meet) to discuss and break it down. Work is delivered on separate branches on GitHub, then goes through Peer Review.
- **Extended Meetings:** Conducted via Google Meet for the purpose of understanding and breaking down complex team tasks.
- **Documentation:** Any pivotal decision made verbally during meetings must be summarized in a WhatsApp message under **#document** to ensure it isn't lost.
### 3.2 Meetings

**Stand-ups**
- **Schedule:** Daily, at a time agreed upon the day before, to allow greater flexibility and accommodate team members' circumstances.
- **Maximum Duration:** 15 minutes mandatory, covering the fixed stand-up agenda below, followed by open time for anyone with questions or clarifications — other members are free to leave once done.
- **Fixed Agenda:** Each member briefly answers 3 questions:
    1. What did I accomplish since the last meeting?
    2. What will I accomplish before the next meeting?
    3. Are there any blockers requiring team intervention? *(If the blocker is complex, it can be resolved without a detailed explanation, deferring the discussion to the weekly meeting.)*
- **Attendance:** Mandatory, as much as possible.
  **Other Meetings**
- Longer sessions coordinated in advance for the purpose of dividing tasks or explaining difficult/complex points.
### 3.3 Peer Review SLAs

- **Maximum Review Time:** Commitment to reviewing any Pull Request (PR) within a maximum of **48 hours** from its creation.
- **Reviewer Notification:** A short alert is sent in the WhatsApp group to notify the team that a PR requires their review.
- **Acceptance Criteria:** A minimum of **2 approvals** is required before merging any code into `main` — no exceptions.
- **Review Quality:** Comments must be technical and constructive, explaining the "reason" when requesting changes (Change Requests) rather than unjustified rejection.
- **Priority:** PRs that resolve blockers for another team member are given immediate review priority, regardless of role.