# Git Workflow & Commit Conventions(sara alajjouri)

## 1. Branch Types & Naming Conventions

### 1.1 `main` (Production Branch)
- Contains production-ready code.
- Direct commits are strictly **forbidden**.
- Protected branch: Requires approval from maintainers and passing CI/CD pipelines.

### 1.2 `develop` (Integration Branch)
- Primary development branch containing the latest delivered features for the next release.
- Merges into `main` during release cycles.

### 1.3 `feature/<short-description>`
- **Cut from:** `develop`
- **Merges back into:** `develop`
- **Purpose:** Developing new functionality or user stories.
- **Example:** `feature/user-authentication`, `feature/cart-payment-gateway`

### 1.4 `fix/<short-description>` / `bugfix/<short-description>`
- **Cut from:** `develop`
- **Merges back into:** `develop`
- **Purpose:** Resolving non-critical bugs found during development/QA.
- **Example:** `fix/null-pointer-login`, `fix/profile-image-upload`

### 1.5 `hotfix/<short-description>`
- **Cut from:** `main`
- **Merges back into:** `main` AND `develop`
- **Purpose:** Emergency patches for production issues.
- **Example:** `hotfix/security-patch-v1.0.1`

---

## 2. Branch Naming Rules

```
<type>/<short-description>
```

- `<type>` must match the same types used in commits (`feat`, `fix`, `refactor`, etc.)
- `<short-description>` **must always be in English**, written in kebab-case (lowercase words separated by `-`), with no spaces, special characters, or non-English text.
- No vague abbreviations (e.g. `fx`, `updt`) — the name must be clear and understandable to any team member.

### Branch Types Table

| Type | Usage | Example |
|------|-------|---------|
| `feature` | New feature | `feature/user-authentication` |
| `fix` / `bugfix` | Non-critical bug fix | `fix/null-pointer-login` |
| `hotfix` | Emergency production fix | `hotfix/security-patch-v1.0.1` |
| `refactor` | Code restructuring | `refactor/wallet-balance-logic` |
| `chore` | Maintenance (dependencies, config) | `chore/update-gradle-version` |
| `docs` | Documentation-only changes | `docs/update-readme` |
| `test` | Adding/editing tests | `test/auth-viewmodel-unit-tests` |

### Additional Rules
1. **Strictly forbidden**: Arabic names or transliterations (e.g. `feature/tasjeel-dokhool` ❌).
2. The name must reflect the **actual content** of the change, not just a ticket number (avoid `feature/task-123` alone — prefer `feature/task-123-user-authentication`).
3. If there's a clear module scope, include it in the description: `feature/wallet-currency-conversion`.
4. All lowercase, words separated by `-` (not `_` or camelCase).

---

## 3. Strict Commit Message Prefix Rules (Conventional Commits)

To maintain a clean, readable, and parseable Git history, all commit messages **MUST** adhere strictly to the **Conventional Commits** standard using explicit `<type>` prefixes.

### Commit Format Structure
```text
<type>(<scope>): <short summary in imperative mood>
```

### Commit Types (`<type>`)

| Type | When to use it | Example |
|------|-----------------|---------|
| `feat` | Adding a new feature | `feat(auth): add Google sign-in option` |
| `fix` | Fixing a bug | `fix(cart): resolve null pointer on empty checkout` |
| `docs` | Documentation-only changes (README, comments) | `docs(readme): update setup instructions` |
| `style` | Formatting changes that don't affect logic (spacing, semicolons) | `style(login): fix indentation in LoginScreen` |
| `refactor` | Restructuring code without changing its behavior | `refactor(wallet): simplify balance calculation logic` |
| `perf` | Performance improvements | `perf(list): optimize recyclerview rendering` |
| `test` | Adding or editing tests | `test(auth): add unit tests for login viewmodel` |

### Scope Rule (optional but recommended)
`<scope>` identifies the part of the project affected, e.g.: `auth`, `wallet`, `cart`, `ui`, `db`.

```
feat(wallet): add currency conversion support
fix(profile): correct image upload crash on Android 12
```

### Additional Commit Rules
1. Summary **must be in imperative mood** ("add" not "added" or "adding").
2. First letter after `:` must be **lowercase**.
3. No period (`.`) at the end of the summary.
4. Max **72 characters** for the first line.
5. If a change is breaking, add `!` after the type/scope and explain in the body:
   ```
   feat(api)!: change response format for /users endpoint

   BREAKING CHANGE: response now returns paginated object instead of array
   ```

---

## 4. Full Example (Branch + Commits)

```
Branch: feature/wallet-currency-conversion

Commits:
feat(wallet): add currency conversion support
test(wallet): add unit tests for conversion rates
docs(wallet): document conversion API usage
```
# Clean Code Standards (sameera sweedan)

We define clean code as code that is easily understood by all team members.
It should be readable and maintainable by any team member.
Our goal is to build a system characterized by clarity, ease of modification,
and long-term scalability.
By following these simple standards, we make maintenance an efficient, sustainable,
and stress-free process for everyone on the team.

---

## Naming Rules

- **Choose descriptive and searchable names:** Names must be clear, unambiguous, and easy to find throughout the codebase to avoid confusion.
- **Use pronounceable names:** Avoid using obscure or cryptic abbreviations.
- **Replace magic numbers with named constants:** Always use descriptive constants instead of hardcoded raw values.
  *Example:* `const val SESSION_TIMEOUT_MINUTES = 30`
- **Avoid encodings:** Do not append prefixes or type information to variable names.

---

## Functions Rules

- **Do one thing:** Each function must adhere to the Single Responsibility Principle.
- **Use descriptive names:** Function names should accurately describe their action.
- **Prefer fewer arguments:** Minimize the number of parameters a function accepts (ideally 3 or fewer; more than that is a sign the function may need to be refactored, e.g. by grouping parameters into an object).
- **Have no side effects:** Functions should perform their task without altering external state unnecessarily.
- **Avoid flag arguments:** Split methods into independent functions instead of using boolean flags.

---

## Comments Rules

We write comments to clarify the **"why"**, not the **"what"**.

- **Prioritize self-explanatory code:** Write readable code that speaks for itself whenever possible.
- **Add value with comments:** Include comments only when they provide necessary context that the code cannot express alone.
- **Focus on intent:** Explain the reasoning behind complex decisions or non-obvious logic.
- **Maintain clean code:** Rely on proper indentation rather than closing brace comments, and remove dead code entirely (version control preserves history).
- **Highlight consequences:** Use comments to call out critical side effects or potential risks in the code.

---

## Source Code Structure

We organize our code to enhance readability and maintain logical flow.

- **Keep related code together:** Place dependent or similar functions close to each other, and declare variables near their first usage.
- **Maintain vertical flow:**
    - Separate distinct concepts with vertical whitespace.
    - Organize functions in a downward direction (caller above callee).
    - Use white space to group related logic and separate unrelated segments.
- **Optimize readability:**
    - Keep lines short (recommended max 100-120 characters).
    - Avoid horizontal alignment; rely on standard indentation.
    - Maintain consistent indentation throughout the codebase.

---

## Objects and Data Structures

We design classes and data structures to prioritize simplicity and maintainability.

- **Prefer simplicity:** Use simple data structures (like Data Classes in Kotlin) when complex behavior is not required.
- **Encapsulate logic:** Hide internal structures and avoid hybrid designs that mix object behavior with raw data.
- **Maintain single responsibility:** Keep classes small, focused on doing one thing, and minimize the number of instance variables.
- **Ensure loose coupling:** Base classes should remain independent of their derivatives.
- **Favor polymorphic design:** Use many small, specific functions instead of passing flags or codes to control behavior.
- **Prefer instance methods:** Use non-static methods to maintain better object-oriented design and testability.

---

## Error Handling

- **Use exceptions rather than return codes:** Don't force callers to check return values for errors.
- **Avoid returning or passing null when possible:** Consider using default values, optional types, or the Null Object Pattern instead.
- **Never leave catch blocks empty:** Silently swallowing errors hides bugs — always log, handle, or rethrow.
- **Separate error handling from business logic:** A function that handles errors (e.g. via try/catch) should ideally not also do other work — extract the logic into its own function.
- **Provide context with exceptions:** Error messages should explain what failed and why, to make debugging easier.
- **Fail fast:** Validate inputs early and raise errors as close as possible to the source of the problem.

---

## Code Style & Quality Standards

To ensure high-quality, maintainable, and readable code, we strictly adhere to the coding standards and configurations provided by our instructor.

- **Standardized Style:** We follow the approved team style guide, enforced automatically to ensure consistency across all modules.
- **Automated Enforcement:** All code is validated using **ktlint** and **Detekt** configurations as instructed, which handle formatting and static analysis automatically.
- **Mandatory Verification:** Our `Git Hooks` and `CI pipeline` are configured to enforce these standards. Any code that does not meet the specified requirements will be rejected during the merge process.

---

## Testing Rules (F.I.R.S.T. Principles)

- **Fast:** Tests must run quickly to encourage frequent execution.
- **Independent:** Tests should not rely on each other; they must be isolated and runnable in any order.
- **Repeatable:** Tests should yield consistent results in any environment (local, CI, etc.).
- **Self-validating:** Tests must have a clear boolean pass/fail output — no manual inspection required.
- **Timely:** Write tests close to when the production code is written (or before it, if practicing TDD).
- **One assert per test:** Limit each test to a single verification point for clarity.
- **Readable:** Ensure test code is easy to read and intent is clear.

---

## Version Control & Commits

- Write clear, descriptive commit messages that explain just *what* changed.
- Keep commits atomic: each commit should represent one logical, self-contained change.
- Avoid committing commented-out or dead code.
- Branch names should reflect the feature/fix being worked on.

---

## Code Review

- All code must go through a review (Pull Request) before being merged into the main branch.
- Code review is the primary mechanism for enforcing these standards in practice — reviewers should check for adherence to naming, function size, error handling, and testing rules above, not just correctness.
- Reviews should be timely and constructive, focusing on the code, not the author.
- Automated checks (linting, tests, build) should pass before a human review is requested.

---

# 3. Communication & SLAs (Abdelrahman Hayel Shat)

## 3.1 Internal Communications Protocols

- **Daily & Quick Communication:** Via the team's WhatsApp group.
    - The **"🚨 Blocker"** tag is used for any obstacle halting progress, and must be responded to within a maximum of **8 hours** during working hours.
- **Task Management & Tracking:**
    - **Individual Tasks:** The task is received via the app, discussed during the stand-up to confirm understanding, and delivered to the personal repository.
    - **Team Tasks:** The task is received via the app, followed by a dedicated meeting (Google Meet) to discuss and break it down. Work is delivered on separate branches on GitHub, then goes through Peer Review.
- **Extended Meetings:** Conducted via Google Meet for the purpose of understanding and breaking down complex team tasks.
- **Documentation:** Any pivotal decision made verbally during meetings must be summarized in a WhatsApp message under **#document** to ensure it isn't lost.

## 3.2 Meetings

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

## 3.3 Peer Review SLAs

- **Maximum Review Time:** Commitment to reviewing any Pull Request (PR) within a maximum of **48 hours** from its creation.
- **Reviewer Notification:** A short alert is sent in the WhatsApp group to notify the team that a PR requires their review.
- **Acceptance Criteria:** A minimum of **2 approvals** is required before merging any code into `main` — no exceptions.
- **Review Quality:** Comments must be technical and constructive, explaining the "reason" when requesting changes (Change Requests) rather than unjustified rejection.
- **Priority:** PRs that resolve blockers for another team member are given immediate review priority, regardless of role.

---

# 4. Architecture & Project Structure (Maria Zourob)

The project will follow a modular package structure to keep the code organized and maintainable.

## 4.1 Package Structure

```
src/
 ├── main/
 │   ├── kotlin/
 │   │   └── org.byteBloom/
 │   │        ├── models/
 │   │        ├── usecases/
 │   │        ├── repository/
 │   │        ├── services/
 │   │        ├── utils/
 │   │        └── Main.kt
 │   └── resources/
 └── test/
```

## 4.2 Package Responsibilities

- **models** → Contains data classes and domain models.
- **usecases** → Contains business logic.
- **repository** → Handles data access.
- **services** → Contains service implementations.
- **utils** → Shared helper functions.
- **resources** → Configuration files.

## 4.3 Architecture Rules

- Keep business logic inside usecases.
- One responsibility per class.
- Avoid circular dependencies.
- Keep packages independent whenever possible.
- Follow separation of concerns.