# RoboRally Game Client

A Java-based client application for the RoboRally game, implementing a Model-View-Controller (MVC) architecture.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── module-info.java
│   │   └── dk.dtu.compute.course02324.part4.consuming_rest/
│   │       ├── RunRoborallyApp.java
│   │       ├── model/
│   │       │   ├── Game.java
│   │       │   ├── User.java
│   │       │   └── Player.java
│   │       ├── controller/
│   │       │   ├── UserController.java
│   │       │   ├── GameController.java
│   │       │   ├── PlayerController.java
│   │       │   └── RestApiService.java
│   │       └── view/
│   │           ├── MainView.java
│   │           └── dialogs/
│   │               ├── LeaveGameDialog.java
│   │               ├── ShowOnlineGamesDialog.java
│   │               ├── SignUpDialog.java
│   │               ├── GameSignUpDialog.java
│   │               ├── CreateGameDialog.java
│   │               ├── DeleteGameDialog.java
│   │               └── SignInDialog.java
│   └── resources/
└── test/
    └── java/
```

## Architecture Overview

The project follows the Model-View-Controller (MVC) architectural pattern:

- **Model**: Contains the core data structures
  - `Game.java`: Represents game state and logic
  - `User.java`: Handles user information
  - `Player.java`: Manages player-specific data

- **Controller**: Manages business logic and API interactions
  - `UserController.java`: Handles user-related operations
  - `GameController.java`: Manages game operations
  - `PlayerController.java`: Controls player actions
  - `RestApiService.java`: Handles REST API communication

- **View**: Implements the user interface
  - `MainView.java`: Primary application window
  - `dialogs/`: Contains various dialog boxes for user interactions
    - Authentication: `SignInDialog.java`, `SignUpDialog.java`
    - Game Management: `CreateGameDialog.java`, `DeleteGameDialog.java`, `GameSignUpDialog.java`
    - Game Interaction: `ShowOnlineGamesDialog.java`, `LeaveGameDialog.java` 