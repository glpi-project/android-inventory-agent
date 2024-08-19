# Contributing to Android Inventory Agent

Thank you for considering contributing to the Android Inventory Agent! Your contributions help improve the project and ensure its success. Please follow the guidelines below to ensure a smooth and productive collaboration.

---

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [How to Contribute](#how-to-contribute)
    - [Bug Reports](#bug-reports)
    - [Pull Requests](#pull-requests)
3. [Development Environment](#development-environment)
4. [Code Style](#code-style)
5. [Testing](#testing)
6. [Contact](#contact)

---

## Code of Conduct

By participating in this project, you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md). Please treat others with respect and professionalism.

---

## How to Contribute

### Bug Reports

1. **Search existing issues**: Before reporting a bug, check if it has already been reported.
2. **Create a new issue**: If no similar issue exists, open a new issue and provide:
   - A clear and descriptive title.
   - Steps to reproduce the issue.
   - Expected vs. actual behavior.
   - Environment details (e.g., Android version, device model).
3. **Attach logs and screenshots**: If applicable, include logs or screenshots to help diagnose the issue.

### Pull Requests

1. Fork the repository and create a new branch for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Make your changes while adhering to the [Code Style](#code-style) guidelines.
3. Ensure all tests pass and write new tests for your changes.
4. Commit your changes with clear and descriptive messages:
   ```bash
   git commit -m "feat: add new feature description"
   ```
5. Push your branch and create a Pull Request against the `main` branch.

---

## Development Environment

To set up the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/glpi-project/android-inventory-agent.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and resolve dependencies.
4. Build and run the project using the preferred emulator or physical device.

---

## Code Style

- Follow **Android's official Kotlin/Java coding standards**.
- Use meaningful variable, method, and class names.
- Format your code using Android Studio's built-in formatter.
- Add comments to explain complex logic or algorithms.

---

## Testing

### Writing Tests
- Use **JUnit** and / or **Espresso** for writing unit and UI tests.
- Place your tests in the appropriate directories:
  - `<flavor>/src/test/` for unit tests.
  - `<flavor>/src/androidTest/` for instrumentation tests.

### Running Tests
- To run all tests, execute the following command:
  ```bash
  ./gradlew test
  ./gradlew connectedAndroidTest
  ```

### Firebase Test Lab (Optional)
- Tests are automatically run using Firebase Test Lab via GitHub Actions for all Pull Requests.

---

## Contact

For questions or discussions, feel free to:
- Open an issue.
- Join the [GLPI community forums](https://forum.glpi-project.org/).
- Reach out to the maintainers directly on GitHub.

---

We appreciate your interest in improving Android Inventory Agent. Thank you for contributing!
