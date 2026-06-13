# Changelog

All notable changes to this project are documented in this file.

The format is inspired by [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project follows semantic versioning where practical.

## [Unreleased]

## [1.2.0] - 2026-06-13

### Added
- New "Kinetic Logic" (Stitch) design system applied across all screens.
- Bundled `Plus Jakarta Sans` and `Space Grotesk` fonts with full typography scale.
- Glassmorphism cards, ambient gradient glows, and a bottom navigation bar.
- Result screen: status ring, best-solution card, and a share action.
- New app launcher icon (glass hexagon) with adaptive foreground over a dark gradient.

### Changed
- Home screen reworked into mode selection cards (tap a mode to start).
- Game screen redesigned: glowing target panel, glass tiles, dedicated operator/submit panel.
- Settings redesigned with duration chips and a segmented default-mode switch.
- Centralized app versioning via `version.properties`; documented release/signing flow in `README.md`.

### Removed
- Temporary in-app icon preview screen and trial launcher icon variants.

## [1.1.0] - 2026-05-25

### Added
- Multi-module Android project setup (`app-android`, `game-core`) with tests.
- Game modes (`Standard`, `Za decu`) and local statistics tracking.
- Enhanced UI flow (home, rules, settings, game, result) with improved layout.
- Launcher icon variants and in-app icon preview screen.
- Release signing scaffold (`keystore.properties.example`, Gradle signing config support).

### Changed
- Improved solver expression rendering (fewer redundant parentheses).
- Increased solver time budget for better best-solution quality.
- Rebalanced kids mode to be easier but still interesting:
  - target range `10-100`
  - numbers `4 mala + 1 srednji`
  - operators `+ - * / ( )`

### Fixed
- Safe top insets for screens to avoid overlap with camera/notch.
- Kids mode button/operator layout visibility issues.
