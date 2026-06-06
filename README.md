# EyeGuard 👁️

A lightweight desktop utility for Windows and macOS that helps prevent
Computer Vision Syndrome by reminding you to follow the **20-20-20 rule**.

> Every 20 minutes, look at something 20 feet away for 20 seconds.

## Features
- Silent system tray app — zero interruption to your workflow
- Smart 20-minute countdown timer
- Full-screen calming eye break overlay with 20-second countdown
- Auto-pause during fullscreen apps (presentations, video calls)
- Auto-pause when system is idle
- Working hours schedule — no reminders outside your work day
- Daily compliance stats and streak tracking
- Configurable intervals, sounds, and behaviour

## Requirements
- Java 21 LTS
- Windows 10+ or macOS 11+

## Build and Run

Clone the repository:
  git clone https://github.com/yourusername/eyeguard.git
  cd eyeguard

Build:
  mvn clean install

Run:
  mvn javafx:run

## Tech Stack
- Java 21 LTS
- OpenJFX 21 (JavaFX)
- Maven 3.9+
- SLF4J + Logback
- Jackson 2
- JUnit 5

## License
MIT — see LICENSE file for details.

## Contributing
Pull requests are welcome. Please open an issue first to discuss
what you would like to change.
