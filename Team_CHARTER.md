
# Clean Code Standards (sameera sweedan)

We define clean code as code that is easily understood by all team members.
It should be readable and maintainable by any team member.
Our goal is to build a system characterized by clarity, ease of modification,
and long-term scalability.
By following these simple standards, we make maintenance an efficient, sustainable,
and stress-free process for everyone on the team.

---

## Naming Rules
-## Naming Rules

- **Choose descriptive and searchable names:** Names must be clear, unambiguous, and easy to find throughout the codebase to avoid confusion.
- **Use pronounceable names:** Avoid using obscure or cryptic abbreviations.
- **Replace magic numbers with named constants:** Always use descriptive constants instead of hardcoded raw values.
  *Example:* `const val SESSION_TIMEOUT_MINUTES = 30`
- **Avoid encodings:** Do not append prefixes or type information to variable names.
- **Replace magic numbers with named constants**: Always use descriptive constants instead of raw numbers.
- **Avoid encodings**: Do not append prefixes or type information to variable names.

---

## Functions Rules
- **Do one thing**: Each function must adhere to the Single Responsibility Principle.
- **Use descriptive names**: Function names should accurately describe their action.
- **Prefer fewer arguments**: Minimize the number of parameters a function accepts (ideally 3 or fewer; more than that is a sign the function may need to be refactored, e.g. by grouping parameters into an object).
- **Have no side effects**: Functions should perform their task without altering external state unnecessarily.
- **Avoid flag arguments**: Split methods into independent functions instead of using boolean flags.

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
- **Use exceptions rather than return codes**: Don't force callers to check return values for errors.
- **Avoid returning or passing null when possible**: Consider using default values, optional types, or the Null Object Pattern instead.
- **Never leave catch blocks empty**: Silently swallowing errors hides bugs — always log, handle, or rethrow.
- **Separate error handling from business logic**: A function that handles errors (e.g. via try/catch) should ideally not also do other work — extract the logic into its own function.
- **Provide context with exceptions**: Error messages should explain what failed and why, to make debugging easier.
- **Fail fast**: Validate inputs early and raise errors as close as possible to the source of the problem.

---
## Code Style & Quality Standards

To ensure high-quality, maintainable, and readable code, we strictly adhere to the coding standards and configurations provided by our instructor.

* **Standardized Style:** We follow the approved team style guide, enforced automatically to ensure consistency across all modules.
* **Automated Enforcement:** All code is validated using **ktlint** and **Detekt** configurations as instructed, which handle formatting and static analysis automatically.
* **Mandatory Verification:** Our `Git Hooks` and `CI pipeline` are configured to enforce these standards. Any code that does not meet the specified requirements will be rejected during the merge process.
---

## Testing Rules (F.I.R.S.T. Principles)
- **Fast**: Tests must run quickly to encourage frequent execution.
- **Independent**: Tests should not rely on each other; they must be isolated and runnable in any order.
- **Repeatable**: Tests should yield consistent results in any environment (local, CI, etc.).
- **Self-validating**: Tests must have a clear boolean pass/fail output — no manual inspection required.
- **Timely**: Write tests close to when the production code is written (or before it, if practicing TDD).
- **One assert per test**: Limit each test to a single verification point for clarity.
- **Readable**: Ensure test code is easy to read and intent is clear.

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
