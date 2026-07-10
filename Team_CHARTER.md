## Architecture & Project Structure

The project will follow a modular package structure to keep the code organized and maintainable.

### Package Structure

```
src/
 ├── main/
 │   ├── kotlin/
 │   │   └── org.logiroute/
 │   │        ├── models/
 │   │        ├── usecases/
 │   │        ├── repository/
 │   │        ├── services/
 │   │        ├── utils/
 │   │        └── Main.kt
 │   └── resources/
 └── test/
```

### Package Responsibilities

- **models** → Contains data classes and domain models.
- **usecases** → Contains business logic.
- **repository** → Handles data access.
- **services** → Contains service implementations.
- **utils** → Shared helper functions.
- **resources** → Configuration files.

### Architecture Rules

- Keep business logic inside usecases.
- One responsibility per class.
- Avoid circular dependencies.
- Keep packages independent whenever possible.
- Follow separation of concerns.