# Contributing to Metal Shader Support Plugin

Thank you for your interest in contributing! This document provides guidelines and instructions.

## Code of Conduct

- Be respectful and constructive
- Welcome newcomers and help them learn
- Focus on what is best for the community

## Getting Started

### Prerequisites

- JDK 21 or higher
- IntelliJ IDEA (Community or Ultimate) 2024.1+
- Git

### Setup Development Environment

1. Fork and clone the repository:
```bash
git clone https://github.com/YOUR_USERNAME/metal-shader-support-plugin.git
cd metal-shader-support-plugin
```

2. Open the project in IntelliJ IDEA

3. Run the plugin in development mode:
```bash
./gradlew runIde
```

## Development Workflow

### Project Structure

```
metal-shader-support-plugin/
├── src/main/kotlin/com/metalshader/
│   ├── language/       # Core language support
│   ├── highlighting/   # Syntax highlighting
│   ├── completion/     # Code completion
│   └── annotator/      # Error detection
├── src/main/resources/
│   ├── META-INF/       # Plugin descriptor
│   ├── icons/          # UI icons
│   └── liveTemplates/  # Code templates
└── src/test/kotlin/    # Unit tests
```

### Making Changes

1. Create a feature branch:
```bash
git checkout -b feature/your-feature-name
```

2. Make your changes following the coding standards

3. Add tests for new functionality

4. Run tests:
```bash
./gradlew test
```

5. Build the plugin:
```bash
./gradlew buildPlugin
```

6. Commit your changes:
```bash
git commit -m "feat: add your feature description"
```

### Commit Message Format

Follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting)
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

Examples:
```
feat: add support for Metal 3.2 raytracing keywords
fix: correct vector swizzle validation
docs: update installation instructions
```

## Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

## Testing

- Write unit tests for new features
- Ensure all tests pass before submitting PR
- Test manually in the IDE

## Submitting Pull Requests

1. Push your branch to your fork:
```bash
git push origin feature/your-feature-name
```

2. Open a Pull Request on GitHub

3. Fill out the PR template with:
   - Description of changes
   - Related issue number
   - Screenshots (if UI changes)
   - Testing performed

4. Wait for review and address feedback

## Reporting Bugs

Use the [GitHub Issues](https://github.com/JirakJ/metal-shader-support-plugin/issues) with:

- Clear title
- Steps to reproduce
- Expected vs actual behavior
- IDE version and OS
- Code samples (if applicable)

## Feature Requests

Open an issue with:

- Use case description
- Proposed solution
- Examples from other tools (if any)

## Questions?

- Open a [Discussion](https://github.com/JirakJ/metal-shader-support-plugin/discussions)
- Email: support@jakubjirak.com

Thank you for contributing! 🎉
