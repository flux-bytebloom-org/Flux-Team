#Flux
# Clean Code Standards (Member 2)

We define clean code as code that is easily understood by all team members.
It should be readable and maintainable by anyone, not just the original author.
Our goal is to build a system characterized by clarity, ease of modification,
and long-term scalability.
By following these simple standards, we make maintenance an efficient, sustainable,
and stress-free process for everyone on the team.

---

## Naming Rules
- **Choose descriptive and unambiguous names**: Names must clearly state their purpose.
- **Make meaningful distinctions**: Ensure names are distinct to avoid confusion.
- **Use pronounceable names**: Avoid using obscure or cryptic abbreviations.
- **Use searchable names**: Choose names that are easy to find throughout the codebase.
- **Replace magic numbers with named constants**: Always use descriptive constants instead of raw numbers.
- **Avoid encodings**: Do not append prefixes or type information to variable names.

---

## Functions Rules
- **Keep it small**: Aim for concise, focused functions. As a guideline, a function should ideally not exceed ~20 lines; if it grows beyond that, consider breaking it down.
- **Do one thing**: Each function must adhere to the Single Responsibility Principle.
- **Use descriptive names**: Function names should accurately describe their action.
- **Prefer fewer arguments**: Minimize the number of parameters a function accepts (ideally 3 or fewer; more than that is a sign the function may need to be refactored, e.g. by grouping parameters into an object).
- **Have no side effects**: Functions should perform their task without altering external state unnecessarily.
- **Avoid flag arguments**: Split methods into independent functions instead of using boolean flags.

---

## Comments Rules
- **Explain in code**: Prioritize readable code over comments.
- **Avoid redundancy**: Do not comment on what is already obvious from the code.
- **No noise**: Keep comments meaningful; avoid trivial details.
- **No closing brace comments**: Keep braces clean; rely on indentation.
- **Delete dead code**: Never comment out code; remove it completely (version control keeps history).
- **Explain intent**: Use comments to explain why something is done, not what.
- **Clarify complexity**: Use comments to explain difficult or non-obvious logic.
- **Warn of consequences**: Highlight parts of the code that have critical side effects.

---

## Source Code Structure
- Separate concepts vertically.
- Related code should appear vertically dense.
- Declare variables close to their usage.
- Dependent functions should be close to each other.
- Similar functions should be close to each other.
- Place functions in the downward direction (caller above callee).
- Keep lines short (recommended max ~100–120 characters).
- Don't use horizontal alignment.
- Use white space to associate related things and disassociate weakly related ones.
- Don't break indentation.

---

## Objects and Data Structures
- Hide internal structure.
- Prefer data structures when behavior isn't needed.
- Avoid hybrid structures (half object and half data).
- Keep classes small.
- Each class should do one thing.
- Keep a small number of instance variables.
- Base classes should know nothing about their derivatives.
- It's better to have many small functions than to pass flags/codes into a function to select behavior.
- Prefer non-static methods to static methods.

---

## Error Handling
- **Use exceptions rather than return codes**: Don't force callers to check return values for errors.
- **Avoid returning or passing null when possible**: Consider using default values, optional types, or the Null Object Pattern instead.
- **Never leave catch blocks empty**: Silently swallowing errors hides bugs — always log, handle, or rethrow.
- **Separate error handling from business logic**: A function that handles errors (e.g. via try/catch) should ideally not also do other work — extract the logic into its own function.
- **Provide context with exceptions**: Error messages should explain what failed and why, to make debugging easier.
- **Fail fast**: Validate inputs early and raise errors as close as possible to the source of the problem.

---

## Formatting & Style Consistency
- The team follows a single, agreed-upon style guide for each language in use (e.g. PEP8 for Python, Airbnb style for JavaScript/TypeScript, Google Style Guide for Java/C++).
- Formatting is enforced automatically via a linter/formatter (e.g. ESLint + Prettier, Black, Checkstyle) rather than relying on manual discipline.
- A pre-commit hook or CI pipeline step should verify formatting and linting rules before code is merged, so style consistency isn't optional.

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
- Write clear, descriptive commit messages that explain *why* a change was made, not just *what* changed.
- Keep commits atomic: each commit should represent one logical, self-contained change.
- Avoid committing commented-out or dead code.
- Branch names should reflect the feature/fix being worked on.

---

## Code Review
- All code must go through a review (Pull Request) before being merged into the main branch.
- Code review is the primary mechanism for enforcing these standards in practice — reviewers should check for adherence to naming, function size, error handling, and testing rules above, not just correctness.
- Reviews should be timely and constructive, focusing on the code, not the author.
- Automated checks (linting, tests, build) should pass before a human review is requested.
