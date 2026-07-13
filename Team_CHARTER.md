### Branch Types & Naming Conventions (sara alajjouri)

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

## 3. Strict Commit Message Prefix Rules (Conventional Commits)

To maintain a clean, readable, and parseable Git history, all commit messages **MUST** adhere strictly to the **Conventional Commits** standard using explicit `<type>` prefixes.

### Commit Format Structure
```text
<type>(<scope>): <short summary in imperative mood>
