# Contributing to ItemMagnet

Thank you for contributing!

## Development setup

1. Clone the repository
2. Ensure JDK 25 is available (Gradle toolchains auto-download via Foojay)
3. Build: `./gradlew build`
4. Output JAR: `build/libs/ItemMagnet-1.0.0.jar`

## Pull request process

1. Fork and create a feature branch
2. Keep changes focused and match existing code style
3. Run `./gradlew build` before submitting
4. Update `CHANGELOG.md` under `[Unreleased]` for user-facing changes

## Code style

- Java conventions, descriptive names, early returns
- Use reflection for optional plugin hooks (Lands, WorldGuard, CMI)
- All player-facing strings belong in `messages.yml`
- All gameplay tuning belongs in `config.yml`

## Reporting bugs

Use the GitHub issue template and include Paper version, config snippets, and steps to reproduce.
